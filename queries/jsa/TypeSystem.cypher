MERGE (ts:TypeSystem)

MERGE (ts)-[:`_instance`]->(:Tag:`Undefined`)
MERGE (ts)-[:`_instance`]->(:Tag:`Null`)
MERGE (ts)-[:`_instance`]->(:Tag:`Boolean`)
MERGE (ts)-[:`_instance`]->(:Tag:`Number`)
MERGE (ts)-[:`_instance`]->(:Tag:`String:`)
MERGE (ts)-[:`_instance`]->(:Tag:`Symbol`)
MERGE (ts)-[:`_instance`]->(:Tag:`Object`)
MERGE (ts)-[:`_instance`]->(:Tag:`Function`)
MERGE (ts)-[:`_instance`]->(:Tag:`Error`)

MERGE (ts)-[:`_instance`]->(:Tag:`Math`)
MERGE (ts)-[:`_instance`]->(:Tag:`Date`)
MERGE (ts)-[:`_instance`]->(:Tag:`RegExp`)
MERGE (ts)-[:`_instance`]->(:Tag:`Array`)
MERGE (ts)-[:`_instance`]->(:Tag:`Map`)
MERGE (ts)-[:`_instance`]->(:Tag:`Set`)
MERGE (ts)-[:`_instance`]->(:Tag:`JSON`)
MERGE (ts)-[:`_instance`]->(:Tag:`ArrayBuffer`)
MERGE (ts)-[:`_instance`]->(:Tag:`DataView`)
MERGE (ts)-[:`_instance`]->(:Tag:`Promise`)
MERGE (ts)-[:`_instance`]->(:Tag:`Proxy`)
MERGE (ts)-[:`_instance`]->(:Tag:`Reflect`)

MERGE (ts)-[:`_instance`]->(:Tag:`Infinity`)
