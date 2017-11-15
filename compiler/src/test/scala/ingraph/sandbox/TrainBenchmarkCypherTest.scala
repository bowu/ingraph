package ingraph.sandbox

import ingraph.compiler.CypherToQPlan
import ingraph.compiler.cypher2qplan.CypherParser
import ingraph.emf.util.PrettyPrinter
import org.scalatest.FunSuite
import org.slizaa.neo4j.opencypher.{openCypher => oc}

class TrainBenchmarkCypherTest extends FunSuite {

  test("Random test from cypher string") {
    TrainBenchmarkCypherTest.testQueryString(
      """MATCH (segment:Segment)-[e2:Foo|Boo*2..5]-(v2:Bar)-[:Foo2]-()-[:Foo3*1..1]-()
        |RETURN *, segment, "foo" as foo""".stripMargin)

  }

  test("Random double edge variable in the same MATCH") {
    TrainBenchmarkCypherTest.testQueryString(
      """MATCH
        |  (a)-[e]->(b),
        |  (a)-[e]->(b)
        |RETURN a, e, b""".stripMargin)

  }

  test("Random double edge variable in separate MATCHes") {
    TrainBenchmarkCypherTest.testQueryString(
      """MATCH (a)-[e]->(b)
        |MATCH (a)-[e]->(b)
        |RETURN a, e, b""".stripMargin)

  }

//  for (queryFile <- new File("queries/trainbenchmark-simple").listFiles()) {
//    test(queryFile.getName.dropRight(".cypher".length)) {
//      val query = FileUtils.readFileToString(queryFile)
//      val ep = JPlanParser.parse(query)
//    }
//  }


  test("PosLength.cypher") {
    TrainBenchmarkCypherTest.testQueryFile("PosLength")
  }

  test("ConnectedSegments.cypher") {
    TrainBenchmarkCypherTest.testQueryFile("ConnectedSegments")
  }

  test("RouteSensor.cypher") {
    TrainBenchmarkCypherTest.testQueryFile("RouteSensor")
  }

  test("SwitchMonitored.cypher") {
    TrainBenchmarkCypherTest.testQueryFile("SwitchMonitored")
  }

  test("SwitchSet.cypher") {
    TrainBenchmarkCypherTest.testQueryFile("SwitchSet")
  }

  test("SemaphoreNeighbor.cypher") {
    TrainBenchmarkCypherTest.testQueryFile("SemaphoreNeighbor")
  }
}

object TrainBenchmarkCypherTest {
  val queryPackPath = "trainbenchmark/"
  val printCypher = true
  val printQPlan = true
  val skipResolve = false
  val skipBeautify = false

  def testQueryFile(queryName: String): Unit = {
    val cypher = CypherParser.parseFile(queryPackPath + queryName)

    testQueryCommon(cypher, queryName)
  }

  def testQueryString(queryString: String, queryName: String = "ad-hoc query"): Unit = {
    val cypher = CypherParser.parseString(queryString)

    testQueryCommon(cypher, queryName)
  }

  def testQueryCommon(cypher: oc.Cypher, queryName: String): Unit = {
    if (printCypher) println(PrettyPrinter.format(cypher))

    val qplan = CypherToQPlan.build_IKnowWhatImDoing(cypher, queryName, skipResolve = skipResolve, skipBeautify = skipBeautify)

    if (printQPlan) println(qplan)
  }
}
