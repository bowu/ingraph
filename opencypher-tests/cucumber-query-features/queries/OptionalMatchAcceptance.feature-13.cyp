
      MATCH (a:B)
      OPTIONAL MATCH (a)-[r]-(a)
      RETURN r