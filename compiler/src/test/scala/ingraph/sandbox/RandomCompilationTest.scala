package ingraph.sandbox

import ingraph.compiler.test.{CompilerTest, CompilerTestConfig}
import ingraph.model.expr
import ingraph.model.fplan.{AllDifferent, GetVertices, Production, Projection}
import ingraph.model.gplan

class RandomCompilationTest extends CompilerTest {
  override val config = CompilerTestConfig(querySuitePath = None
    , compileGPlanOnly = false
    , skipGPlanResolve = false
    , skipGPlanBeautify = false
    , printQuery = false
    , printCypher = true
    , printGPlan = true
    , printNPlan = true
    , printFPlan = true
  )

  test("Random test for label check") {
    val stages = compile(
      """MATCH (te:TrackElement)
        |RETURN te:Segment""".stripMargin)
  }

  test("Random test from cypher string") {
    compile(
      """MATCH (n)
        |WITH n.id + 1 AS new_id
        |CREATE (:Label {id: new_id}) """.stripMargin)

  }

  test("Random double edge variable in the same MATCH") {
    compile(
      """MATCH
        |  (a)-[e]->(b),
        |  (a)-[e]->(b)
        |RETURN a, e, b""".stripMargin)

  }

  test("Random double edge variable in separate MATCHes") {
    compile(
      """MATCH (a)-[e]->(b)
        |MATCH (a)-[e]->(b)
        |RETURN a, e, b""".stripMargin)

  }

  test("Random: no edge variable in pattern") {
    compile(
      """MATCH (a)
        |OPTIONAL MATCH (a)-->(b)
        |WHERE 1=2
        |RETURN b""".stripMargin
    )
  }

  ignore("Random: create w/ vertex and edge properties") {
    compile(
      """CREATE (:Foo {a: 23, b: 'bar'})-[:FooEdge {a:11, c:'edge'}]->(:Foo2), (:Foo3)
        |RETURN 1 AS one""".stripMargin
    )
  }

  test("Random: vertex pattern with property map") {
    compile(
      """MATCH (n {id: "n1"})
        |WHERE n.length=1
        |RETURN n""".stripMargin
    )
  }

  ignore("Random: create after match") {
    compile(
      """MATCH (n:Foo)
        |CREATE (n)-[:Bar]->(m:Foo2)-[:Bar2]->(o:Foo3)
        |RETURN n, m""".stripMargin
    )
  }

  ignore("Random: various expressions") {
    compile(
      """MATCH (segment:Segment), (foo:Foo), (bar:Bar)
        |WHERE 1=1
        |  AND segment.name STARTS WITH 'alibaba'
        |  AND 'rablo' ENDS WITH segment.name
        |  AND 'mese' CONTAINS 'uveghegy'
        |  AND segment.length <= 0
        |  AND 2<3
        |  AND NOT 1=1
        |  OR 0=1
        |  OR 1=1
        |  AND sin(3)+4*2^6 > segment.length
        |  AND sin(segment.length) > 2
        |  AND segment.length IS NULL
        |RETURN DISTINCT *, sum(segment.width) as w, segment as l, [1, segment.length, cos(3)] as list, count(*) as ize
        |ORDER BY 2+3 DESC, l.width ASC""".stripMargin
    )
  }

  test("Random: expression IS NULL") {
    compile(
      """MATCH (n:Person)
        |WHERE n.name IS NULL
        |RETURN n, n.name""".stripMargin
    )
  }

  test("Random: expression IS NOT NULL") {
    compile(
      """MATCH (n:Person)
        |WHERE n.name IS NOT NULL
        |RETURN n, n.name""".stripMargin
    )
  }

  test("Random: LIMIT w/o SKIP") {
    compile(
      """MATCH (p:Person)
        |RETURN p
        |ORDER BY 1
        |LIMIT 10""".stripMargin
    )
  }

  test("Random: SKIP w/o LIMIT") {
    compile(
      """MATCH (p:Person)
        |RETURN p
        |ORDER BY 1
        |SKIP 3""".stripMargin
    )
  }

  test("Random: SKIP w/ LIMIT") {
    compile(
      """MATCH (p:Person)
        |RETURN p
        |ORDER BY 1
        |SKIP 3
        |LIMIT 10""".stripMargin
    )
  }

  test("Random: ORDER BY Person.name, w/o SKIP/LIMIT") {
    compile(
      """MATCH (p:Person)
        |RETURN p
        |ORDER BY p.name""".stripMargin
    )
  }

  test("Random: ORDER BY multiple expressions, w/o SKIP/LIMIT") {
    compile(
      """MATCH (p:Person)
        |RETURN p
        |ORDER BY p.age, p.age asc, p.name desc""".stripMargin
    )
  }

  test("Random: ORDER BY with aliases exchanged") {
    compile(
      """MATCH (p:Person)-->(c:Car)
        |RETURN p as c, c as p
        |ORDER BY c.name""".stripMargin
    )
  }

  // See: slizaa/slizaa-opencypher-xtext#24
  ignore("Random: WITH..WHERE") {
    compile(
      """MATCH (p:Person)-->(c:Car)
        |WITH p, count(c) as carNumber
        |WHERE carNumber > 1
        |MATCH (p)-->(c:Car)
        |RETURN p, c
      """.stripMargin
    )
  }

  test("Random: WITH..WHERE simple") {
    compile(
      """MATCH (p:Person)-->(c:Car)
        |WITH p, count(c) as carNumber
        |WHERE carNumber > 1
        |RETURN p
      """.stripMargin
    )
  }

  test("Random: variable length path") {
    compile(
      """MATCH (n1)-[p*3..]->(n2)
        |RETURN n1, n2
      """.stripMargin)
  }

  test("Random: 2-part query featuring WITH") {
    compile(
      """MATCH (p:Person)
        |WITH DISTINCT p
        |MATCH (p)-->(c:Car)
        |RETURN p, c
      """.stripMargin
    )
  }

  test("Random: compile list literal") {
    compile(
      """RETURN [1, 2+3, 'foo', 'bar'] AS l
      """.stripMargin)
  }

  test("Random: compile IN expression") {
    compile(
      """MATCH (p:Person)
        |WHERE 'en' IN p.languages
        |RETURN p.name, p.languages
      """.stripMargin)
  }

  test("Random: compile STARTS WITH") {
    compile(
      """RETURN 'foo' STARTS WITH 'f' AS b
      """.stripMargin)
  }

  test("Random: compile ENDS WITH") {
    compile(
      """RETURN 'foo' ENDS WITH 'oo' AS b
      """.stripMargin)
  }

  test("Random: compile CONTAINS") {
    compile(
      """RETURN 'bar' CONTAINS 'a' AS b
      """.stripMargin)
  }

  test("Random: compile regexp matching") {
    compile(
      """RETURN 'foo' =~ 'o+' AS b
      """.stripMargin)
  }

  test("Random: multiple labels in the label predicate") {
    compile(
      """MATCH (p)
        |WHERE p:Person:Teacher
        |RETURN p
      """.stripMargin
    )
  }

  test("Random: property lookup and multiple labels in the label predicate") {
    compile(
      """MATCH (p)
        |WHERE p.foo:Person:Teacher
        |RETURN p
      """.stripMargin
    )
  }

  ignore("Random: property lookup on map and multiple labels in the label predicate") {
    compile(
      """MATCH (p)
        |WHERE {foo: p}.foo:Person:Teacher
        |RETURN p
      """.stripMargin
    )
  }

  ignore("Random: multiple property lookup on map") {
    compile(
      """MATCH (p)
        |RETURN p, {foo: {innerFoo: p}}.foo.innerFoo
      """.stripMargin
    )
  }

  ignore("Random: multiple property lookup on map and multiple labels in the label predicate") {
    compile(
      """MATCH (p)
        |WHERE {foo: {innerFoo: p}}.foo.innerFoo:Person:Teacher
        |RETURN p
      """.stripMargin
    )
  }

  ignore("Random: REMOVE") {
    compile(
      """MATCH (a:Person {name: 'Alice'})-[:KNOWS]->(b:Person {name: 'Bob'})
        |REMOVE CASE WHEN a.age>b.age THEN a ELSE b END.age.hair.boot
        |RETURN a, b
      """.stripMargin
    )
  }

  test("Random: count(*)") {
    compile(
      """MATCH (a:Person {name: 'Alice'})-[:KNOWS]->(b:Person)
        |RETURN a, count(*)
      """.stripMargin
    )
  }

  test("Random: DELETE friends of Alice") {
    compile(
      """MATCH (a:Person {name: 'Alice'})-[:KNOWS]->(b:Person)
        |DETACH DELETE b
      """.stripMargin
    )
  }

  test("Random: MATCH...CREATE based on matched value") {
    compile(
      """MATCH (a:Person {name: 'Alice'}), (b:Person)
        |CREATE (a)-[:KNOWS {foo: a.age}]->(b)
      """.stripMargin
    )
  }

  test("should compile MATCH") {
    val stages = compile("MATCH (n) RETURN n, n.foo")
    assert(stages.gplan.asInstanceOf[gplan.Production].child.asInstanceOf[gplan.Projection].projectList.length==2)
  }

  // see note in the GPlanResolver for resolving RETURN *
  test("should compile MATCH RETURN *") {
    val stages = compile("MATCH (n)-->(m) RETURN *")
    assert(stages.gplan.asInstanceOf[gplan.Production].child.asInstanceOf[gplan.Projection].projectList.length==1)
  }

  test("should identify aggregation criteria after aggregation in RETURN") {
    val stages = compile(
      """MATCH
        |  (message:Message)-[:IS_LOCATED_IN]->(destination:Country)
        |RETURN
        |  count(message) AS messageCount,
        |  destination.name,
        |  destination.population
        |""".stripMargin)
    assert(stages.gplan.asInstanceOf[gplan.Production].child.asInstanceOf[gplan.Grouping].aggregationCriteria.length==2)
  }

  test("should resolve aliased property lookup two query parts later") {
    compile(
      """MATCH (n)
        |WITH n.foo as nFoo
        |WITH nFoo
        |RETURN nFoo
        |""".stripMargin)
  }

  test("should compile simple index expression") {
    compile(
      """MATCH (n)
        |RETURN n.languages[0] AS firstLanguage
        |""".stripMargin)
  }

  test("should compile index range expression") {
    compile(
      """MATCH (n)
        |RETURN n.languages[2..5] AS someLanguages
        |""".stripMargin)
  }

  test("should compile index range expression missing lower bound") {
    compile(
      """MATCH (n)
        |RETURN n.languages[..5] AS someLanguages
        |""".stripMargin)
  }

  test("should compile index range expression missing upper bound") {
    compile(
      """MATCH (n)
        |RETURN n.languages[2..] AS someLanguages
        |""".stripMargin)
  }

  test("should resolve index expressions referenced in patterns") {
    compile(
      """MATCH (n:Person)
        |WITH collect(n) as persons
        |WITH persons[0] as onePerson
        |OPTIONAL MATCH (onePerson)-[:KNOWS]->(old:Person {age: 100})
        |RETURN onePerson, old
        |""".stripMargin)
  }

  test("should compile MATCH and UNWIND") {
    compile("MATCH (n) UNWIND n.favColors AS favColor RETURN n, favColor")
  }

  test("should compile MATCH and multiple UNWINDs") {
    compile(
      """MATCH (n)
        |UNWIND n.favColors AS favColor
        |UNWIND n.favMovies AS favMovie
        |RETURN n, favColor, favMovie
        |""".stripMargin)
  }

  test("should compile vertices COLLECT'ed then UNWIND as a vertex") {
    compile(
      """MATCH (n:Person)
        |WITH collect(n) as persons
        |UNWIND persons AS p2
        |MATCH (p2)-[:OWNER]->(c:Car)
        |RETURN persons, p2, c
        |""".stripMargin)
  }

  test("should resolve edgelistattribute") {
    val stages = compile(
      """MATCH (post:Post)-[:REPLY_OF*]->(message:Message)
        |RETURN post.id, message.id
        |""".stripMargin)

    assert(stages.gplan.find(p => p.isInstanceOf[gplan.Expand] ).get.asInstanceOf[gplan.Expand].edge.asInstanceOf[expr.EdgeListAttribute].resolvedName.isDefined)
  }

  test("should resolve projected vertex fed into DELETE") {
    val stages = compile(
      """MATCH (n:Person)
        |WITH n
        |ORDER BY n.id
        |LIMIT 1
        |DELETE n
        |""".stripMargin)

    assert(stages.gplan.find(p => p.isInstanceOf[gplan.Delete] ).get.asInstanceOf[gplan.Delete].attributes(0).resolvedName.isDefined)
  }

  test("should retain ORDER BY attributes in projection") {
    val stages = compile(
      """WITH 1 AS x, 2 AS y
        |RETURN x
        |ORDER BY y
        |LIMIT 1
        |""".stripMargin)

    assert(stages.gplan.find(p => p.isInstanceOf[gplan.Sort] ).get.asInstanceOf[gplan.Sort].child.asInstanceOf[gplan.Projection]
      .projectList.foldLeft(false)( (acc, ri) => acc || ri.resolvedName.get.resolvedName.equals("y#0")))
  }

  test("should retain the property itself for ORDER BY properties in projection") {
    val stages = compile(
      """MATCH (a:A)-[r:REL]->(x:B)
        |RETURN a
        |ORDER BY x.foo
        |LIMIT 1
        |""".stripMargin)

    assert(stages.gplan.find(p => p.isInstanceOf[gplan.Sort] ).get.asInstanceOf[gplan.Sort].child.asInstanceOf[gplan.Projection]
      .projectList.foldLeft(false)( (acc, ri) => acc || ri.resolvedName.get.resolvedName.equals("x.foo#0")))
  }

  test("should allow (compile) RETURN DISTINCT even is doing ORDER BY on the unaliased version of an aliased returnitem") {
    val stages = compile(
      """MATCH (a:A)
        |RETURN DISTINCT a.foo
        |ORDER BY a.foo
        |""".stripMargin)
  }

  test("should allow (compile) RETURN DISTINCT even is doing ORDER BY on a property of the unaliased version of an aliased returnitem") {
    val stages = compile(
      """MATCH (a:A)
        |RETURN DISTINCT a as bar
        |ORDER BY a.foo
        |""".stripMargin)
  }

  test("should not introduce additional projection for ORDER BY on attribute already present in the projection") {
    val stages = compile(
      """MATCH   (tag:Tag)
        |WITH     tag.name AS tagName
        |RETURN   tagName
        |ORDER BY tagName ASC
        |""".stripMargin)

    // get the gplan tree and cast to the desired types
    val productionOp = stages.gplan.asInstanceOf[gplan.Production]
    val sortOp = productionOp.child.asInstanceOf[gplan.Sort]
    val returnProjectionOp = sortOp.child.asInstanceOf[gplan.Projection]
    val withProjectionOp = returnProjectionOp.child.asInstanceOf[gplan.Projection]
    val getverticesOp = withProjectionOp.child.asInstanceOf[gplan.GetVertices]
    assert( Option(getverticesOp).isDefined )
    // last projection and the sorting has a single item...
    assert( returnProjectionOp.projectList.length == 1)
    assert( sortOp.order.length == 1)
    // ... and they are the same
    assert( returnProjectionOp.projectList(0).child.asInstanceOf[expr.ResolvableName].resolvedName == sortOp.order(0).child.asInstanceOf[expr.ResolvableName].resolvedName)
  }


  test("should not introduce additional projection for ORDER BY on property whose base element is already present in the projection") {
    val stages = compile(
      """MATCH   (tag:Tag)
        |RETURN   tag
        |ORDER BY tag.name ASC
        |""".stripMargin)

    // get the gplan tree and cast to the desired types
    val productionOp = stages.gplan.asInstanceOf[gplan.Production]
    val sortOp = productionOp.child.asInstanceOf[gplan.Sort]
    val returnProjectionOp = sortOp.child.asInstanceOf[gplan.Projection]
    val getverticesOp = returnProjectionOp.child.asInstanceOf[gplan.GetVertices]
    assert( Option(getverticesOp).isDefined )
    // last projection and the sorting has a single item...
    assert( returnProjectionOp.projectList.length == 1)
    assert( sortOp.order.length == 1)
    // ... and the latter is a property of the former
    assert( returnProjectionOp.projectList(0).child.asInstanceOf[expr.ResolvableName].resolvedName == sortOp.order(0).child.asInstanceOf[expr.PropertyAttribute].elementAttribute.resolvedName)
  }

  test("should not duplicate aliased return item's base expression in the introduced additional projection for ORDER BY on unaliased item") {
    val stages = compile(
      """MATCH (p:Person)
        |RETURN p.name AS name
        |ORDER BY p.name
        |LIMIT 1
        |""".stripMargin)

    // get the gplan tree and cast to the desired types
    val productionOp = stages.gplan.asInstanceOf[gplan.Production]
    val returnProjectionOp = productionOp.child.asInstanceOf[gplan.Projection]
    val topOp = returnProjectionOp.child.asInstanceOf[gplan.Top]
    val sortOp = topOp.child.asInstanceOf[gplan.Sort]
    val introducedProjectionOp = sortOp.child.asInstanceOf[gplan.Projection]
    val getverticesOp = introducedProjectionOp.child.asInstanceOf[gplan.GetVertices]
    assert( Option(getverticesOp).isDefined )
    // last projection and the sorting has a single item and the introduced has 2 items
    assert( returnProjectionOp.projectList.length == 1)
    assert( sortOp.order.length == 1)
    assert( introducedProjectionOp.projectList.length == 2)
    // ... and the latter contains the sort key
    assert( returnProjectionOp.projectList.foldLeft[Boolean](false)( (acc, pi) => {
        acc || (pi.child.asInstanceOf[expr.ResolvableName].resolvedName == sortOp.order(0).child.asInstanceOf[expr.ResolvableName].resolvedName)
      })
    )
  }

  test("AllDifferent operator that require edges of different types to be different should be removed upon beautification.") {
    val stages = compile(
      """MATCH (p1:Person)-[:KNOWS]-(p2:Person)-[:INTEREST|MASTER_OF]->(t:Topic)-[:CLASS]->(c1:Class)-[:SUBCLASS_OF]->(c2:Class)
        |RETURN p1.name AS p1_name, p2.name as p2_name
        |""".stripMargin)

    assert( !nodeExistByType(stages.gplan, classOf[gplan.AllDifferent]) )
  }

  test("AllDifferent operator for edge lists should be retained.") {
    val stages = compile(
      """MATCH (p1:Person)-[:KNOWS]-(p2:Person)-[:INTEREST|MASTER_OF]->(t:Topic)-[:CLASS]->(c1:Class)-[:SUBCLASS_OF*1..]->(c2:Class)
        |RETURN p1.name AS p1_name, p2.name as p2_name
        |""".stripMargin)

    assert( nodeExistByType(stages.gplan, classOf[gplan.AllDifferent]) )
  }

  test("AllDifferent operator on overlapping edge types should be retained.") {
    val stages = compile(
      """MATCH (p1:Person)-[:KNOWS]-(p2:Person)-[:KNOWS|FRIEND]-(p3:Person)
        |RETURN p1.name AS p1_name, p2.name as p2_name, p3.name as p3_name
        |""".stripMargin)

    assert( nodeExistByType(stages.gplan, classOf[gplan.AllDifferent]) )
  }

  test("AllDifferent operator should be retained since edge without edge type overlaps every edge type.") {
    val stages = compile(
      """MATCH (p1:Person)-[:KNOWS]-(p2:Person)-[]-(p3:Person)
        |RETURN p1.name AS p1_name, p2.name as p2_name, p3.name as p3_name
        |""".stripMargin)

    assert( nodeExistByType(stages.gplan, classOf[gplan.AllDifferent]) )
  }
}

/** Random compiler tests that must stop after GPlan compilation.
  *
  * This is because of limitations of the following stages.
  * See note for each of the cases.
  *
  * On the long term, this should contain no test cases.
  */
class RandomGPlanCompilationTest extends CompilerTest {
  override val config = CompilerTestConfig(querySuitePath = None
    , compileGPlanOnly = true
    , skipGPlanResolve = false
    , skipGPlanBeautify = false
    , printQuery = false
    , printCypher = true
    , printGPlan = true
    , printNPlan = true
    , printFPlan = true
  )

  /* FPlan error:
'Unwind unwindattribute(listexpression(1, 2, 3), li, Some(li#0))
+- Dual
 (of class ingraph.model.nplan.Unwind)
scala.MatchError: 'Unwind unwindattribute(listexpression(1, 2, 3), li, Some(li#0))
+- Dual
 (of class ingraph.model.nplan.Unwind)
	at ingraph.compiler.gplan2nplan.SchemaInferencer$.transform(SchemaInferencer.scala:18)
   */
  test("Random: compile UNWIND w/ list literal") {
    compile(
      """UNWIND [1, 2, 3] as li
        |RETURN li AS l
      """.stripMargin)
  }

  test("Random: compile map literal") {
    compile(
      """MATCH (person1)<-[:HAS_CREATOR]-(m:Message)<-[:LIKES]-()
        |RETURN {person: person1, message: m} AS c
      """.stripMargin)
  }

  test("Complex create query") {
    compile(
      """MATCH (c:City {id:1226})
        |CREATE (p:Person {id: 10995116277777, firstName: 'Almira', lastName: 'Patras', gender: 'female', birthday: 19830628, creationDate: 20101203163954934, locationIP: '193.104.227.215', browserUsed: 'Internet Explorer', speaks: ['ru', 'en'], emails: ['Almira10995116277777@gmail.com', 'Almira10995116277777@gmx.com']})-[:IS_LOCATED_IN]->(c)
        |WITH p, count(*) AS dummy1
        |UNWIND [1916] AS tagId
        |    MATCH (t:Tag {id: tagId})
        |    CREATE (p)-[:HAS_INTEREST]->(t)
        |WITH p, count(*) AS dummy2
        |UNWIND [[53, 49]] AS s
        |    MATCH (u:Organisation {id: s[0]})
        |    CREATE (p)-[:STUDY_AT {classYear: s[1]}]->(u)
        |WITH p, count(*) AS dummy3
        |UNWIND [] AS w
        |    MATCH (comp:Organisation {id: w[0]})
        |    CREATE (p)-[:WORKS_AT {workFrom: w[1]}]->(comp)
      """.stripMargin)
  }

  test("Indexing array in MATCH") {
    val stages = compile(
      """UNWIND [[20, 30]] AS tuple
        |MATCH (org:Organisation {id: tuple[0]})
        |RETURN org.id
      """.stripMargin)
    printlnSuppressIfIngraph(stages.toString)
  }

}
