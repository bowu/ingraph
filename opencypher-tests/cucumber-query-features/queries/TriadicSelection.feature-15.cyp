
      MATCH (a:A)-[:KNOWS|FOLLOWS]->(b)-->(c)
      OPTIONAL MATCH (a)-[r:KNOWS]->(c)
      WITH c WHERE r IS NOT NULL
      RETURN c.name