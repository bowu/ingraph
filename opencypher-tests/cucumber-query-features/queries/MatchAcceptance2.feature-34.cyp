
      MATCH (n)
      OPTIONAL MATCH (n)-[:NOT_EXIST]->(x)
      RETURN n, x