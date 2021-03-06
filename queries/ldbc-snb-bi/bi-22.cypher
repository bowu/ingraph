// Q22. International dialog
/*
  :param {
    country1: 'Mexico',
    country2: 'Indonesia'
  }
*/
MATCH
  (country1:Country {name: $country1})<-[:IS_PART_OF]-(city1:City)<-[:IS_LOCATED_IN]-(person1:Person),
  (country2:Country {name: $country2})<-[:IS_PART_OF]-(city2:City)<-[:IS_LOCATED_IN]-(person2:Person)
WITH person1, person2, city1, 0 AS score
// subscore 1
OPTIONAL MATCH (person1)<-[:HAS_CREATOR]-(c:Comment)-[:REPLY_OF]->(:Message)-[:HAS_CREATOR]->(person2)
WITH DISTINCT person1, person2, city1, score + (CASE c WHEN null THEN 0 ELSE  4 END) AS score
// subscore 2
OPTIONAL MATCH (person1)<-[:HAS_CREATOR]-(m:Message)<-[:REPLY_OF]-(:Comment)-[:HAS_CREATOR]->(person2)
WITH DISTINCT person1, person2, city1, score + (CASE m WHEN null THEN 0 ELSE  1 END) AS score
// subscore 3
OPTIONAL MATCH (person1)-[k:KNOWS]-(person2)
WITH DISTINCT person1, person2, city1, score + (CASE k WHEN null THEN 0 ELSE 15 END) AS score
// subscore 4
OPTIONAL MATCH (person1)-[:LIKES]->(m:Message)-[:HAS_CREATOR]->(person2)
WITH DISTINCT person1, person2, city1, score + (CASE m WHEN null THEN 0 ELSE 10 END) AS score
// subscore 5
OPTIONAL MATCH (person1)<-[:HAS_CREATOR]-(m:Message)<-[:LIKES]-(person2)
WITH DISTINCT person1, person2, city1, score + (CASE m WHEN null THEN 0 ELSE  1 END) AS score
// preorder
ORDER BY
  city1.name ASC,
  score DESC,
  person1.id ASC,
  person2.id ASC
WITH
  city1,
  [score, person1.id, person2.id] AS topTuple
WITH
  city1,
  collect(topTuple) AS tops
WITH
  city1,
  tops[0] AS top
WITH
  top[1] AS person1Id,
  top[2] AS person2Id,
  city1,
  top[0] AS score
RETURN
  person1Id,
  person2Id,
  city1.name,
  score
ORDER BY
  score DESC,
  person1Id ASC,
  person2Id ASC
