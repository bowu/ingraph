
      MATCH (a)
      WHERE NOT a.name CONTAINS 'b'
      RETURN a