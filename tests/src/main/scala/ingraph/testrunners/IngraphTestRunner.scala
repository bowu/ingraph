package ingraph.testrunners

import java.util.concurrent.TimeUnit

import com.google.common.base.Stopwatch
import ingraph.driver.CypherDriverFactory
import ingraph.driver.data.{IngraphQueryHandler, ResultCollectingChangeListener}
import ingraph.ire.{Indexer, OneTimeQueryAdapter}
import ingraph.tests.LdbcSnbTestCase
import org.supercsv.prefs.CsvPreference

class IngraphTestRunner(tc: LdbcSnbTestCase) {

  val driver = CypherDriverFactory.createIngraphDriver()
  val session = driver.session()

  def run() : List[Map[String, Any]] = {
    val csvPreference = new CsvPreference.Builder('"', '|', "\n").build
    val queryHandler = session.registerQuery(tc.name, tc.querySpecification)
    val sLoad = Stopwatch.createStarted()
    val listener = new ResultCollectingChangeListener(queryHandler.keys())
    queryHandler.registerDeltaHandler(listener)
    queryHandler.readCsv(
      tc.vertexCsvPaths,
      tc.edgeCsvPaths,
      csvPreference
    )
    val queryTime = sLoad.elapsed(TimeUnit.NANOSECONDS)

    val indexer = queryHandler.adapter.indexer
    val updateTimes = tc.updates.map { updateQuery =>
      val s = Stopwatch.createStarted()
      update(updateQuery, "upd", indexer, queryHandler, listener)
      s.elapsed(TimeUnit.NANOSECONDS)
    }.toList

    println(tc.sf + "," + tc.query + ",ingraph," + queryTime + "," + updateTimes.mkString(","))
    queryHandler.result()
  }

  def update(querySpecification: String,
             queryName: String,
             indexer: Indexer,
             queryHandler: IngraphQueryHandler,
             listener: ResultCollectingChangeListener): List[Map[String, Any]] = {
    val adapter = new OneTimeQueryAdapter(querySpecification, queryName, indexer)
    adapter.results()
    adapter.close()
    val results = queryHandler.result
    listener.terminated()
    return results
  }

  def close(): Unit = {
    session.close()
    driver.close()
  }

}
