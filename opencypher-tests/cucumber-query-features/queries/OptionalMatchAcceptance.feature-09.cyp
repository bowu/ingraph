
      MATCH (a:X)
      OPTIONAL MATCH (a)-->(b:Y)
      RETURN b