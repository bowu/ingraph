package ingraph.ire

import org.neo4j.driver.internal.InternalRelationship
import org.neo4j.driver.internal.InternalNode
import org.neo4j.driver.v1.Value

import scala.collection.JavaConverters._
import org.scalatest.FunSuite

class DataManipulationTest extends FunSuite {

  test("One off modifier queries work") ({
    val indexer = new Indexer()
    val oneOff = """MATCH (t:Train)-[r:ON]->(seg1:Segment)-[:NEXT]->(seg2:Segment)
                  |DELETE r
                  |CREATE (t)-[:ON]->(seg2)""".stripMargin
    val whereIsTrain = "MATCH (t:Train)-[r:ON]->(s:Segment) RETURN s"
    
    val v1 = new IngraphVertex(1L, Set("Train"), Map[String, Value]())
    val v2 = new IngraphVertex(2L, Set("Segment"), Map[String, Value]())
    val v3 = new IngraphVertex(3L, Set("Segment"), Map[String, Value]())

    indexer.addVertex(v1)
    indexer.addVertex(v2)
    indexer.addVertex(v3)

    indexer.addEdge(new IngraphEdge(4L, v1, v2, "ON"  ))
    indexer.addEdge(new IngraphEdge(5L, v2, v3, "NEXT"))
    indexer.addEdge(new IngraphEdge(6L, v3, v2, "NEXT"))

    val whereIsAdapter = new IngraphIncrementalAdapter(whereIsTrain, "something", indexer)
    for (i <- 1 to 10 ) {
      assert(whereIsAdapter.result() == List(Vector(2)))
      new IngraphSearchAdapter(oneOff, "remove", indexer).terminate()
      assert(whereIsAdapter.result() == List(Vector(3)))
      new IngraphSearchAdapter(oneOff, "remove", indexer).terminate()
    }
  })

}
