package ingraph.compiler.sql.driver

import java.io.File

import ingraph.compiler.sql.Util._
import ingraph.compiler.sql.driver.LdbcTest.{expectedToSucceed, ldbcQueries, ldbcSqlQueries}
import ingraph.compiler.sql.driver.SqlDriver.ExternalDatabase
import org.cytosm.common.gtop.GTop
import org.cytosm.common.gtop.io.SerializationInterface
import org.scalatest.FunSuite

import scala.collection.JavaConverters._
import scala.reflect.io

class LdbcTest extends FunSuite {
  val gTop: GTop = SerializationInterface.read(new File(getClass.getResource("/gtop/ldbc.gtop").getFile))
  val url = "jdbc:postgresql://localhost:5432/ldbcsf1?user=postgres&password=foo"

  test("SQL") {
    withResources(
      new SqlDriver(
        translateCreateQueries = true,
        gTop = Some(gTop),
        database = ExternalDatabase(url),
        initializeDb = false)) {
      driver =>
        withResources(driver.session) { session =>
          withResources(session.beginTransaction()) { tx =>
            withResources(tx.rawSqlConnection.createStatement()) { statement =>
              SqlDriver.dump(statement, "SELECT * FROM person LIMIT 20")

              withResources(statement.executeQuery("SELECT count(*) FROM person")) { resultSet =>
                assert(resultSet.next())
                val count = resultSet.getInt(1)
                assert(!resultSet.next())

                assertResult(46)(count)
              }
            }
          }
        }
    }
  }

  (Seq(
    "Simple test" -> "MATCH (n:Person) RETURN n.id ORDER BY n.id LIMIT 10"
  ) ++ ldbcQueries).foreach { case (name, cypherQueryString) =>
    test(name) {
      withResources(
        new SqlDriver(
          translateCreateQueries = true,
          gTop = Some(gTop),
          database = ExternalDatabase(url),
          initializeDb = false)) {
        driver =>
          withResources(driver.session) { session =>
            withResources(session.beginTransaction()) { tx =>
              val limit = 20
              val referenceSqlQuery = ldbcSqlQueries.find(_._1 == name).map(_._2)
              val stringBuilder = StringBuilder.newBuilder
              val printlnFunc: String => Unit = str => {
                stringBuilder.append(str)
                stringBuilder.append('\n')
              }

              val rowCount = referenceSqlQuery.map(query =>
                withResources(tx.rawSqlConnection.createStatement()) { statement =>
                  SqlDriver.dump(statement, query, Some(limit), printlnFunc)
                })

              try {
                println()

                val limit = 20
                val result = tx.run(cypherQueryString).list().asScala

                for (record <- result.take(limit)) {
                  for ((key, value) <- record.asMap().asScala) {
                    println(s"""$key = $value""")
                  }
                  println("---")
                }

                println(s"Totally: ${result.size} rows (only first $limit)")
                println("----------------------------------")

                if (referenceSqlQuery.isDefined) {
                  println("vvvvvv REFERENCE RESULT vvvvvv")
                  print(stringBuilder)

                  assertResult(rowCount.get)(result.size)
                }

              } catch {
                case throwable: Throwable if !expectedToSucceed.contains(name) => cancel(name + " has failed. Only warning!", throwable)
              }
            }
          }
      }
    }
  }
}

object LdbcTest {
  val ldbcQueriesPath = "/interactive-tests"

  val expectedToSucceed: Set[String] = Set(
    "SQL",
    "Simple test",
    "LdbcQuery1",
    "LdbcQuery6",
    "LdbcQuery8",
    "LdbcQuery11",
    "LdbcQuery12",
    "LdbcShortQuery1PersonProfile",
    "LdbcShortQuery3PersonFriends",
    "LdbcShortQuery4MessageContent",
    "LdbcShortQuery5MessageCreator",
    "LdbcShortQuery7MessageReplies",
    ""
  )

  val ldbcQueries: Seq[(String, String)] = getQueries("cypher")
  val ldbcSqlQueries: Seq[(String, String)] = getQueries("sql")

  private def getQueries(extension: String): Seq[(String, String)] = {
    new File(getClass.getResource(ldbcQueriesPath).getFile)
      .listFiles()
      .filter(_.isFile)
      .sortBy { file =>
        val filename = file.getName
        // sort by name then by ID
        filename.replaceAll("""\d+.*$""", "") -> Integer.parseInt(filename.replaceAll("""\D""", ""))
      }
      .map(io.File(_))
      .filter(f => f.hasExtension(extension))
      .map(f => f.stripExtension -> f.slurp())
      .toSeq
  }
}
