
      OPTIONAL MATCH (a:NotThere)
      OPTIONAL MATCH (b:NotThere)
      WITH a, b
      OPTIONAL MATCH (b)-[r:NOR_THIS]->(a)
      RETURN a, b, r