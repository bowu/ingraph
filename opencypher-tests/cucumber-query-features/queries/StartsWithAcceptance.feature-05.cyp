
      MATCH (a)
      WHERE a.name STARTS WITH 'a'
        AND a.name ENDS WITH 'f'
      RETURN a