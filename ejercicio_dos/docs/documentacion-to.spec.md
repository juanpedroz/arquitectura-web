# Especificación — Registro de Estudiantes (Integrador 2)

> Documento de especificación derivado de `plan-integrador-estudiantes.md`.
> Su propósito es servir de contrato de implementación: describe **qué** debe
> existir y **cómo se verifica**, sin fijar rutas de archivo ni código
> definitivo. La implementación se hace en una sesión posterior con su propio OK.
>
> Estado: **listo para implementar**.

---

## Problem Statement

La facultad necesita llevar un registro de sus estudiantes y de las carreras en
las que están inscriptos, pero hoy esa información vive dispersa en tres
archivos `.csv` sueltos (`estudiantes.csv`, `carreras.csv`,
`estudianteCarrera.csv`) que nadie puede consultar de forma útil.

Con los archivos como única fuente no se puede:

- dar de alta un estudiante nuevo ni matricularlo en una carrera sin editar
  texto plano a mano, arriesgando duplicados de DNI o de libreta universitaria;
- responder preguntas cotidianas de secretaría académica ("¿quién tiene la
  libreta 61607?", "¿qué estudiantes de Rauch cursan TUDAI?");
- saber qué carreras concentran más inscriptos;
- producir el reporte anual de inscriptos y egresados por carrera que se pide
  para la gestión académica.

Además, los datos de origen están sucios: hay años de inscripción truncados,
graduaciones anteriores a la inscripción, un DNI mal tipeado que no existe en el
padrón de estudiantes, un par estudiante–carrera repetido y el género escrito en
dos idiomas. Cualquier consulta que se haga sobre ellos tal como están devuelve
resultados incorrectos.

## Solution

Una aplicación Java con persistencia JPA/Hibernate sobre MySQL que:

1. **Modela el dominio** con tres entidades — `Estudiante`, `Carrera` e
   `Inscripcion` (entidad asociativa que carga los atributos propios de la
   relación N:M: año de inscripción, año de graduación y antigüedad).
2. **Carga los tres CSV** al arrancar, normalizando el género y **rechazando con
   log** las filas que no son confiables, sin inventar datos para reemplazarlas.
3. **Expone las siete operaciones de la consigna** (alta, matriculación y cinco
   consultas) a través de repositorios, resueltas **mayormente en JPQL** y no en
   código Java.
4. **Genera el reporte** de inscriptos y egresados por carrera y año, ordenado
   alfabéticamente por carrera y cronológicamente por año.
5. **Muestra todo por consola** desde un `Main` secuencial que ejecuta cada
   operación una tras otra, con formato tabular legible.

Se entregan además el diagrama de objetos y el DER en mermaid (`.mmd`) más sus
imágenes exportadas.

---

## User Stories

### Modelo y persistencia

1. Como docente que corrige el integrador, quiero un diagrama de objetos que
   muestre las tres clases y sus relaciones, para verificar que el modelo de
   dominio está bien pensado antes de mirar el código.
2. Como docente que corrige el integrador, quiero un diagrama entidad–relación
   con claves primarias, foráneas y únicas, para verificar que el mapeo
   relacional se corresponde con el modelo de objetos.
3. Como desarrollador del grupo, quiero que la relación estudiante–carrera se
   modele como una entidad asociativa `Inscripcion`, para poder guardar los
   atributos propios de esa relación (inscripción, graduación, antigüedad).
4. Como desarrollador del grupo, quiero que `Estudiante` tenga un ID
   autogenerado además del DNI, para no depender de una clave natural que puede
   venir mal cargada desde el CSV.
5. Como secretaría académica, quiero que el DNI de un estudiante sea único en el
   sistema, para que no exista la misma persona dos veces.
6. Como secretaría académica, quiero que el número de libreta universitaria sea
   único, para poder usarlo como identificador de búsqueda sin ambigüedad.
7. Como desarrollador del grupo, quiero que el estado "se graduó" se derive del
   año de graduación en lugar de guardarse aparte, para que no puedan existir
   registros inconsistentes (graduado sin año, o año sin graduación).
8. Como desarrollador del grupo, quiero que las colecciones de inscripciones se
   carguen de forma perezosa, para no traer toda la base a memoria cada vez que
   leo un estudiante.
9. Como desarrollador del grupo, quiero que el esquema se recree en cada
   arranque, para que ninguna corrida arrastre datos residuales de la anterior.

### Carga de datos

10. Como usuario de la aplicación, quiero que los tres CSV se carguen
    automáticamente al arrancar, para no tener que preparar la base a mano.
11. Como desarrollador del grupo, quiero que los CSV se lean desde el classpath
    del proyecto, para que la aplicación funcione igual en cualquier máquina del
    grupo sin configurar rutas absolutas.
12. Como analista de datos, quiero que el género se normalice a un vocabulario
    único durante la carga, para que la consulta por género no devuelva
    resultados parciales por estar escrito en dos idiomas.
13. Como analista de datos, quiero que las filas con año de inscripción mal
    formado se rechacen con un mensaje de log, para saber exactamente qué se
    descartó y por qué.
14. Como analista de datos, quiero que las filas cuyo año de graduación sea
    anterior al de inscripción se rechacen con un mensaje de log, para que el
    reporte de egresados no cuente egresos imposibles.
15. Como analista de datos, quiero que las inscripciones que apuntan a un DNI
    inexistente en el padrón se rechacen con un mensaje de log, para no crear
    estudiantes fantasma.
16. Como analista de datos, quiero que un segundo par estudiante–carrera
    repetido se rechace con un mensaje de log, para que nadie figure dos veces
    inscripto en la misma carrera.
17. Como analista de datos, quiero que los errores de tipeo evidentes se
    corrijan en el CSV de origen y quede constancia de la corrección, para
    recuperar filas válidas sin fabricar datos.
18. Como desarrollador del grupo, quiero un resumen al final de la carga con
    cuántas filas se insertaron y cuántas se rechazaron por cada archivo, para
    confirmar de un vistazo que la carga fue la esperada.
19. Como desarrollador del grupo, quiero que la carga no se repita si la base ya
    tiene datos, para poder ejecutar consultas sin recargar todo.

### Consulta a) — Alta de estudiante

20. Como secretaría académica, quiero dar de alta un estudiante con sus datos
    personales, para incorporarlo al registro.
21. Como secretaría académica, quiero que el alta falle con un mensaje claro si
    el DNI ya existe, para no duplicar una persona.
22. Como secretaría académica, quiero que el alta falle con un mensaje claro si
    la libreta universitaria ya existe, para no romper la búsqueda por libreta.
23. Como secretaría académica, quiero dar de alta un estudiante sin asignarle
    carrera todavía, para poder registrarlo antes de que decida qué cursar.

### Consulta b) — Matriculación

24. Como secretaría académica, quiero matricular a un estudiante existente en
    una carrera existente, para registrar que empezó a cursarla.
25. Como secretaría académica, quiero que la matriculación falle si el
    estudiante no existe, para no crear inscripciones huérfanas.
26. Como secretaría académica, quiero que la matriculación falle si la carrera
    no existe, para no inscribir a nadie en algo que no se dicta.
27. Como secretaría académica, quiero que la matriculación falle si el
    estudiante ya está inscripto en esa misma carrera, para no duplicar la
    inscripción.
28. Como secretaría académica, quiero que al matricular se registre el año
    actual como año de inscripción, antigüedad cero y sin graduación, para no
    tener que cargar esos datos a mano.
29. Como secretaría académica, quiero poder matricular al mismo estudiante en
    más de una carrera, para reflejar a quienes cursan dos títulos.

### Consulta c) — Listado ordenado

30. Como secretaría académica, quiero listar todos los estudiantes ordenados por
    apellido y luego por nombre, para recorrer el padrón como una lista de clase.
31. Como docente que corrige, quiero que el ordenamiento lo resuelva la consulta
    y no el código Java, para verificar que se usó JPQL como pide la consigna.

### Consulta d) — Búsqueda por libreta universitaria

32. Como secretaría académica, quiero buscar un estudiante por su número de
    libreta universitaria, para encontrarlo rápido cuando se presenta en
    ventanilla.
33. Como secretaría académica, quiero que la búsqueda por libreta indique
    claramente que no hay resultados si la libreta no existe, en lugar de fallar.

### Consulta e) — Búsqueda por género

34. Como analista de datos, quiero recuperar todos los estudiantes de un género
    determinado, para producir estadísticas de composición del alumnado.
35. Como analista de datos, quiero que el género sea un parámetro de la
    consulta y no un valor fijo, para poder consultar cualquiera de los géneros
    presentes en los datos.
36. Como analista de datos, quiero que la consulta contemple los géneros no
    binarios que aparecen en el padrón, para que ningún estudiante quede fuera
    de las estadísticas.

### Consulta f) — Carreras por cantidad de inscriptos

37. Como gestión académica, quiero ver las carreras que tienen estudiantes
    inscriptos ordenadas de mayor a menor cantidad, para saber dónde se
    concentra la demanda.
38. Como gestión académica, quiero que las carreras sin ningún inscripto no
    aparezcan en ese listado, porque la consigna pide solo las que tienen
    estudiantes.
39. Como gestión académica, quiero que el listado muestre el nombre de la
    carrera junto a su cantidad, para leerlo sin tener que cruzar identificadores.
40. Como gestión académica, quiero que dos carreras con la misma cantidad de
    inscriptos aparezcan en orden alfabético entre sí, para que el listado sea
    estable entre corridas.

### Consulta g) — Estudiantes por carrera y ciudad

41. Como gestión académica, quiero listar los estudiantes de una carrera
    determinada filtrando por ciudad de residencia, para planificar la oferta de
    cursada en cada sede.
42. Como gestión académica, quiero que ese listado devuelva solo los datos que
    necesito ver y no la entidad completa con sus relaciones, para que la salida
    sea liviana y legible.
43. Como gestión académica, quiero que la combinación carrera y ciudad sean
    parámetros de la consulta, para reutilizar la misma operación con cualquier
    combinación.

### Reporte (punto 3)

44. Como gestión académica, quiero un reporte que muestre, por carrera, la
    cantidad de inscriptos por año, para ver la evolución de la matrícula.
45. Como gestión académica, quiero que el mismo reporte muestre la cantidad de
    egresados por año, para ver cuántos terminan y cuándo.
46. Como gestión académica, quiero que los inscriptos se cuenten por su año de
    inscripción y los egresados por su año de graduación, porque son hechos
    distintos que ocurren en años distintos.
47. Como gestión académica, quiero que las inscripciones sin graduar no cuenten
    como egresados, para que el reporte no infle los egresos.
48. Como gestión académica, quiero que las carreras aparezcan en orden
    alfabético, para encontrar la mía sin recorrer todo el reporte.
49. Como gestión académica, quiero que dentro de cada carrera los años vayan en
    orden cronológico, para leer la serie temporal de corrido.
50. Como gestión académica, quiero que un año en el que hubo inscriptos pero
    ningún egresado (o al revés) aparezca igual con el otro valor en cero, para
    no confundir "cero" con "faltante".

### Ejecución y entrega

51. Como usuario de la aplicación, quiero que el `Main` ejecute todas las
    operaciones una tras otra sin pedirme nada por teclado, para ver el
    resultado completo con una sola corrida.
52. Como docente que corrige, quiero que cada bloque de salida diga a qué punto
    de la consigna corresponde, para seguir la corrección sin leer el código.
53. Como usuario de la aplicación, quiero que la salida esté formateada en
    columnas alineadas, para poder leerla en la terminal.
54. Como desarrollador del grupo, quiero que la base se cree sola si no existe,
    para no tener que preparar el entorno antes de la primera corrida.
55. Como integrante del grupo, quiero un README con el stack y el paso a paso
    para ejecutar, para poder correr el proyecto sin preguntarle a quien lo
    escribió.
56. Como integrante del grupo, quiero que el trabajo del Integrador 1 quede
    intacto, porque ya está entregado.

---

## Implementation Decisions

### Proyecto y stack

- Se crea un **proyecto Maven nuevo** dentro de `ejercicio_dos/`, independiente
  del Integrador 1, que no se toca.
- **Java 25**, declarado en `maven.compiler.source` y `maven.compiler.target`.
  Se toma como referencia el `pom.xml` del Integrador 1, que ya está en 25.
- **Jakarta Persistence 3.1 con Hibernate 6.4.x** sobre **MySQL 8**.
- **Lombok** para reducir el boilerplate de las entidades.
- **Librería de CSV a confirmar en la implementación:** el plan menciona
  *opencsv*, pero el Integrador 1 de este mismo repositorio usa
  *commons-csv*. Cualquiera de las dos resuelve el caso; la decisión es usar
  **una sola** y dejarla registrada. Ante la duda, se prefiere `commons-csv`
  por continuidad con el repositorio.
- Unidad de persistencia propia (`estudiantes`), con
  `hibernate.hbm2ddl.auto = create-drop` y URL de conexión con
  `createDatabaseIfNotExist=true`.

### Entidades

- **`Estudiante`**: identificador autogenerado por identidad, más `dni`
  (único), `nombre`, `apellido`, `edad`, `genero`, `ciudad` y `lu` (único e
  indexado). Colección perezosa de inscripciones mapeada por el lado inverso.
- **`Carrera`**: identificador autogenerado, `nombre`, `duracion` en años y
  colección perezosa de inscripciones mapeada por el lado inverso.
- **`Inscripcion`**: entidad asociativa con identificador autogenerado,
  referencias a `Estudiante` y `Carrera`, más `inscripcion` (año), `graduacion`
  (año, **`0` significa no graduado**) y `antiguedad` (años).
- **`dni` y `lu` se modelan como texto**, no como número, para tolerar ceros a
  la izquierda y no perder información al parsear.
- **El género se modela como texto libre**, no como enumeración: los datos traen
  nueve variantes (incluidas `Polygender`, `Genderfluid`, `Non-binary`,
  `Bigender`, `Agender`) y una enumeración cerrada dejaría estudiantes afuera.
- **`esGraduado()` es un método derivado** de `Inscripcion`, no una columna:
  devuelve verdadero cuando el año de graduación es distinto de cero. Esto evita
  que existan filas contradictorias.
- La regla "un estudiante no se matricula dos veces en la misma carrera" se
  valida **en la lógica de negocio**, no con una clave compuesta, porque la
  clave primaria de `Inscripcion` es autogenerada.

### Repositorios

- Un repositorio por entidad, cada uno con **interfaz e implementación
  separadas**, siguiendo el patrón del Integrador 1.
- Las siete operaciones de la consigna viven en los repositorios; el `Main` solo
  las invoca y formatea la salida.
- **El grueso del trabajo va en JPQL.** Ordenamientos, agrupaciones, filtros y
  conteos se resuelven en la consulta. Java se limita a pasar parámetros e
  imprimir. Esto es un requisito explícito de la consigna, no una preferencia.

### Contratos de las operaciones

| Punto | Operación | Entrada | Salida | Reglas |
|---|---|---|---|---|
| a | Alta de estudiante | datos personales del estudiante | el estudiante persistido | falla si el DNI o la LU ya existen |
| b | Matricular | estudiante y carrera | la inscripción creada | falla si alguno no existe o si ya está inscripto; fija año actual, antigüedad 0 y graduación 0 |
| c | Listar estudiantes | — | estudiantes ordenados | orden por apellido y luego nombre, ascendente |
| d | Buscar por libreta | número de libreta | un estudiante o vacío | la LU es única, devuelve a lo sumo uno |
| e | Buscar por género | género | estudiantes de ese género | el género es parámetro, no valor fijo |
| f | Carreras por inscriptos | — | pares carrera–cantidad | solo carreras con al menos un inscripto; orden por cantidad descendente y nombre ascendente para desempatar |
| g | Estudiantes por carrera y ciudad | nombre de carrera y ciudad | datos de los estudiantes | devuelve un DTO, no la entidad |
| 3 | Reporte | — | filas carrera–año–inscriptos–egresados | orden por carrera ascendente y año ascendente |

### DTOs

- **`CarreraCantidadDTO(nombre, cantidad)`** para el punto f.
- **`BusquedaEstudianteDTO(dni, nombre, apellido, edad, genero, ciudad, lu)`**
  para el punto g: evita devolver la entidad completa con sus relaciones.
- **`ReporteCarreraDTO(carrera, anio, inscriptos, egresados)`** para el reporte.
  Esta forma es la **decisión de diseño firme**; la escritura exacta del JPQL que
  la produce (unión de dos agrupaciones, expresión condicional, o dos consultas
  combinadas) puede refinarse durante la implementación siempre que respete la
  forma del DTO y el ordenamiento pedido.
- Los tres se implementan como `record`, porque son datos inmutables de sola
  lectura y se construyen desde la cláusula de proyección de JPQL.

### Carga de datos

- Los tres CSV se copian a los recursos del proyecto y se leen **por classpath**,
  nunca por ruta absoluta.
- **Orden de carga**: primero carreras, después estudiantes, por último
  inscripciones — las inscripciones dependen de que las otras dos ya existan.
- Las inscripciones referencian estudiantes **por DNI** y carreras **por el
  identificador del CSV**; la carga resuelve esas referencias contra lo ya
  persistido y descarta la fila si no encuentra alguna.
- **Normalización de género en carga**: `Masculino` pasa a `Male` y `Femenino` a
  `Female`. Los demás valores se conservan tal cual. Sin esta normalización, la
  consulta e) por `Male` deja afuera a tres estudiantes y la consulta por
  `Female` a uno.
- **Validaciones de fila, todas con log del motivo y del número de fila:**
  - año de inscripción que no tenga cuatro dígitos → rechazo;
  - año de graduación distinto de cero y anterior al de inscripción → rechazo;
  - DNI de la inscripción que no exista en el padrón → rechazo;
  - par estudiante–carrera ya cargado → rechazo del segundo.
- **Corrección puntual en el CSV de origen**: el DNI `6397408` de dos filas de
  inscripciones es un dígito menos que el `63974080` que sí existe en el padrón.
  Es un error de tipeo evidente y se corrige en el archivo, dejando constancia en
  la bitácora. Ningún otro dato se fabrica.
- Al terminar, la carga informa por consola cuántas filas se insertaron y
  cuántas se rechazaron por archivo.

### Estado verificado de los datos de origen

Contado sobre los CSV actuales, para que la implementación sepa qué esperar:

| Archivo | Filas de datos | Observaciones |
|---|---|---|
| `carreras.csv` | 15 | sin problemas; todas tienen al menos un inscripto |
| `estudiantes.csv` | 104 | sin DNI ni LU duplicados; 9 variantes de género, 4 filas en español |
| `estudianteCarrera.csv` | 109 | 7 filas problemáticas (detalle abajo) |

Filas de `estudianteCarrera.csv` que las validaciones deben atrapar:

- **1 fila** con año de inscripción truncado a `202` (fila con identificador 91).
- **4 filas** con graduación anterior a la inscripción (identificadores 51, 52,
  71 y 78).
- **2 filas** con DNI `6397408`, inexistente en el padrón (identificadores 103 y
  104). Con la corrección de tipeo a `63974080` **se recuperan**.
- **1 par duplicado**: el DNI `64472668` figura dos veces en la carrera 7
  (identificadores 79 y 82). Se conserva la primera y se rechaza la segunda.

Tras aplicar la corrección de tipeo, el resultado esperado es **103
inscripciones cargadas y 6 rechazadas**. Este número es la referencia para
verificar que la carga hace lo que dice.

### Ejecución y salida

- **`Main` secuencial**, sin menú ni entrada por teclado: carga los datos y
  después ejecuta cada punto de la consigna en orden, imprimiendo un
  encabezado que identifica el punto.
- Salida **por consola**, en columnas alineadas. No se genera archivo de salida.
- Para demostrar los puntos a) y b), el `Main` da de alta un estudiante nuevo y
  lo matricula, mostrando también qué pasa cuando se intenta repetir el alta o
  la matriculación.

### Documentación entregable

- Los diagramas se entregan **en los dos formatos**: los `.mmd` fuente (ya
  existen en `docs/`) y las imágenes exportadas para incrustar en la
  documentación.
- El `README.md` se actualiza con la consigna, el stack definitivo, los
  requisitos previos y el paso a paso para ejecutar.
- Cada decisión que se tome durante la implementación se registra en
  `docs/uso-de-ia.md`.

---

## Testing Decisions

### Qué es un buen test acá

Un buen test verifica **comportamiento observable desde afuera del módulo**: qué
devuelve una consulta, en qué orden, y qué filas terminaron o no en la base. No
verifica cómo está escrito el JPQL, ni qué métodos internos se llamaron, ni la
estructura de las clases. Un test que se rompe al reescribir una consulta que
sigue devolviendo lo mismo es un mal test.

### Seams propuestos

**Seam principal: las interfaces de los repositorios.** Es el punto más alto
donde el comportamiento sigue siendo verificable de forma determinista. Un test
levanta una unidad de persistencia de prueba, carga un conjunto fijo y pequeño de
estudiantes, carreras e inscripciones, invoca el método del repositorio y compara
el resultado completo — contenido y orden — contra lo esperado. Cubre los siete
puntos de la consigna y el reporte con un solo mecanismo.

**Seam secundario: el cargador de CSV.** Merece su propio seam porque su
comportamiento interesante es *rechazar* filas, y eso no se ve desde los
repositorios: una fila descartada simplemente no está. El cargador debe devolver
un resultado con las cantidades de filas insertadas y rechazadas por archivo, y
ese resultado es lo que se verifica. Esta es una consecuencia de diseño, no un
detalle de test: sin ese valor de retorno el rechazo es invisible y tampoco se
puede informar por consola.

Se descarta deliberadamente un tercer seam sobre el `Main` o sobre el formateo de
salida: verificar texto impreso es frágil y no aporta nada que los otros dos
seams no cubran ya.

### Qué se testea en cada seam

**Repositorios** — un caso por punto de la consigna, más los bordes:

- alta correcta; alta rechazada por DNI repetido; alta rechazada por LU repetida;
- matriculación correcta; rechazo por estudiante inexistente, por carrera
  inexistente y por inscripción ya existente; matriculación en una segunda
  carrera para el mismo estudiante;
- listado completo verificando el orden por apellido y nombre, incluyendo dos
  apellidos iguales para comprobar el desempate;
- búsqueda por libreta existente y por libreta inexistente;
- búsqueda por género, incluyendo un género no binario del padrón;
- carreras por cantidad de inscriptos: que una carrera sin inscriptos no aparezca
  y que dos carreras empatadas queden en orden alfabético;
- estudiantes por carrera y ciudad: que no se cuelen estudiantes de la misma
  carrera en otra ciudad, ni de la misma ciudad en otra carrera;
- reporte: una carrera con inscriptos y egresados en años distintos, un año con
  inscriptos y cero egresados, una inscripción sin graduar que no debe contarse
  como egreso, y el orden alfabético por carrera con años cronológicos dentro.

**Cargador de CSV** — un archivo de prueba chico por cada caso de rechazo:

- año de inscripción mal formado;
- graduación anterior a la inscripción;
- DNI inexistente en el padrón;
- par estudiante–carrera duplicado;
- normalización de `Masculino` y `Femenino`;
- fila válida que efectivamente se inserta.

Como test de integración de una sola corrida, cargar los CSV reales y verificar
que el resultado sea **103 inscripciones insertadas y 6 rechazadas**. Es un
número concreto y verificado que confirma de punta a punta que la carga funciona.

### Prior art

No hay tests automatizados en el repositorio: el Integrador 1 se verificó
ejecutando el `Main` y leyendo la salida. Esta es la primera vez que se
introduce una suite, así que no hay convención previa a respetar y hay que fijar
una: framework de test estándar de la plataforma, unidad de persistencia
separada para los tests, y un conjunto de datos de prueba propio que no dependa
de los CSV reales salvo en el test de integración mencionado.

**Si la consigna no exige tests automatizados**, el criterio mínimo de
verificación es ejecutar el `Main` y contrastar su salida contra los conteos y
ordenamientos descritos en este documento, que ya están verificados contra los
datos reales.

---

## Out of Scope

- **Interfaz gráfica o web.** La salida es por consola.
- **Menú interactivo.** El `Main` corre de principio a fin sin pedir entrada.
- **CRUD completo.** Solo lo que pide la consigna: alta de estudiante y
  matriculación. No hay baja ni modificación de estudiantes, carreras ni
  inscripciones.
- **Actualizar la antigüedad con el paso del tiempo.** Se carga desde el CSV y
  se fija en cero al matricular; nada la recalcula.
- **Registrar la graduación de un estudiante.** El año de graduación viene del
  CSV; no hay operación para graduar a alguien.
- **Autenticación, usuarios o permisos.**
- **Exportar resultados a archivo** (CSV, PDF u otro).
- **Migraciones de esquema.** El esquema se recrea en cada arranque.
- **Optimización de rendimiento.** Con 104 estudiantes y 109 inscripciones no
  hace falta; basta con no incurrir en consultas N+1 evidentes.
- **Tocar `ejercicio_uno/`**, que está entregado.
- **Corregir masivamente los CSV.** Solo se corrige el error de tipeo del DNI
  identificado; el resto de las filas problemáticas se rechaza.

---

## Further Notes

- **La consigna pide explícitamente que las consultas se resuelvan mayormente en
  JPQL.** Es el criterio de corrección más importante del trabajo: filtrar,
  ordenar o agrupar en Java sobre una lista traída completa cuenta como no
  resuelto, aunque el resultado impreso sea correcto.
- **`plan-integrador-estudiantes.md` menciona un proyecto de referencia
  `bibliotecaJPA` que no está en este repositorio.** Es material externo de la
  cátedra. Todo lo que este documento describe se sostiene sin él; las
  referencias de estilo verificables salen del Integrador 1.
- **El JPQL del reporte que aparece en el plan es tentativo.** Usa `UNION`, que
  no todas las implementaciones de JPQL soportan del mismo modo. Lo firme es la
  forma del DTO y el ordenamiento; la implementación elige cómo llegar ahí y
  registra la decisión.
- **El par estudiante–carrera duplicado no figura en el plan original**: se
  detectó al verificar los datos para esta especificación. Es la razón por la que
  la validación de duplicados pasó de ser solo una regla de la operación de
  matricular a ser también una validación de la carga.
- **La normalización de género no es cosmética.** Sin ella, la consulta e) sobre
  `Male` deja tres estudiantes afuera y sobre `Female`, uno. Es un caso donde
  limpiar el dato en la carga evita tener que ensuciar la consulta.
- **El género no se modela como enumeración a propósito.** Los datos traen nueve
  variantes distintas y cerrar el conjunto obligaría a descartar estudiantes o a
  agruparlos en una categoría que no eligieron.
- Los `.mmd` de ambos diagramas ya están en `docs/` y coinciden con el modelo
  descrito acá. Falta exportarlos a imagen para la entrega.
