
      MATCH (a:A)-[:KNOWS]->(b)-->(c)
      OPTIONAL MATCH (a)-[r:FOLLOWS]->(c)
      WITH c WHERE r IS NOT NULL
      RETURN c.name