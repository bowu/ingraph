
      OPTIONAL MATCH (a:NotThere)
      WITH a
      MATCH (b:B)
      WITH a, b
      OPTIONAL MATCH (b)-[r:NOR_THIS]->(a)
      RETURN a, b, r