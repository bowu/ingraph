
      OPTIONAL MATCH ()-[r:DoesNotExist]-()
      DELETE r
      RETURN r