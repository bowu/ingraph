
      MATCH (n:Single)
      OPTIONAL MATCH (n)-[r:TYPE]-(m)
      RETURN m:TYPE