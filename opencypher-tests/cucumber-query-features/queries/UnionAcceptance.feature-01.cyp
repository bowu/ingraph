
      MATCH (a:A)
      RETURN a AS a
      UNION
      MATCH (b:B)
      RETURN b AS a