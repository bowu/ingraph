MATCH
  (route:Route)
    -[f:follows]->(swP:SwitchPosition)
    -[t:target]->(sw:Switch)
    -[m:monitoredBy]->(sensor:Sensor)
WHERE NOT ((route)-[g:requires]->(sensor))
RETURN DISTINCT route, sensor, swP, sw
