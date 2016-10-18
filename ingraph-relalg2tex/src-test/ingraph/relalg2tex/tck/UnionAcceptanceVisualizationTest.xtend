package ingraph.relalg2tex.tck

import org.junit.Test

import ingraph.cypher2relalg.CypherParser
import ingraph.relalg.util.SchemaInferencer
import ingraph.relalg2tex.RelalgTreeSerializer

class UnionAcceptanceVisualizationTest {

    val RelalgTreeSerializer serializer = new RelalgTreeSerializer
    extension SchemaInferencer inferencer = new SchemaInferencer
    
    /*
    Scenario: Should be able to create text output from union queries
    Given an empty graph
    And having executed:
      """
      CREATE (:A), (:B)
      """
    */
    @Test
    def void testUnionAcceptance_01() {
        val container = CypherParser.parseString('''
        MATCH (a:A)
        RETURN a AS a
        UNION
        MATCH (b:B)
        RETURN b AS a
        ''')
        container.addSchemaInformation
        serializer.serialize(container, "UnionAcceptance_01")
    }

    /*
    Scenario: Two elements, both unique, not distinct
    Given an empty graph
    */
    @Test
    def void testUnionAcceptance_02() {
        val container = CypherParser.parseString('''
        RETURN 1 AS x
        UNION ALL
        RETURN 2 AS x
        ''')
        container.addSchemaInformation
        serializer.serialize(container, "UnionAcceptance_02")
    }

    /*
    Scenario: Two elements, both unique, distinct
    Given an empty graph
    */
    @Test
    def void testUnionAcceptance_03() {
        val container = CypherParser.parseString('''
        RETURN 1 AS x
        UNION
        RETURN 2 AS x
        ''')
        container.addSchemaInformation
        serializer.serialize(container, "UnionAcceptance_03")
    }

    /*
    Scenario: Three elements, two unique, distinct
    Given an empty graph
    */
    @Test
    def void testUnionAcceptance_04() {
        val container = CypherParser.parseString('''
        RETURN 2 AS x
        UNION
        RETURN 1 AS x
        UNION
        RETURN 2 AS x
        ''')
        container.addSchemaInformation
        serializer.serialize(container, "UnionAcceptance_04")
    }

    /*
    Scenario: Three elements, two unique, not distinct
    Given an empty graph
    */
    @Test
    def void testUnionAcceptance_05() {
        val container = CypherParser.parseString('''
        RETURN 2 AS x
        UNION ALL
        RETURN 1 AS x
        UNION ALL
        RETURN 2 AS x
        ''')
        container.addSchemaInformation
        serializer.serialize(container, "UnionAcceptance_05")
    }

}
