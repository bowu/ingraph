
      MATCH (a)
      WHERE NOT a.name CONTAINS null
      RETURN a