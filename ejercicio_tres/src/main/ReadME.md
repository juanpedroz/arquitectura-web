# Trabajo Práctico 3 - Spring Boot, JPA, Hibernate

## Descripción

Se desarrolló un monolito con una API REST que se comunica con una base de datos MySQL.
El esquema cuenta con las entidades Estudiante y Carrera, y la entidad Inscripcion, que
surge de la relación entre ambas y guarda el año de inscripción, el de graduación y la antigüedad.

## Inicialización de la Aplicación

1. Levantar un contenedor de MySQL con Docker:
2. Ejecutar la clase `IntegradorTresApplication`.

Al ejecutarla, la aplicación crea las tablas en la base y carga los datos de los archivos
CSV ubicados en `src/main/resources/csv`. Cuando termina la carga, la API queda disponible
en `http://localhost:8080`.

## Endpoints y respuestas

### Índice

1. [Entidad Estudiante](#entidad-estudiante)
2. [Entidad Carrera](#entidad-carrera)
3. [Entidad Inscripcion](#entidad-inscripcion)


### Entidad Estudiante

Para realizar consultas sobre la entidad Estudiante se utiliza la ruta
`http://localhost:8080/estudiante`.


| Método | Ruta                                       | Descripción | Respuesta exitosa |
|--------|--------------------------------------------|-------------|-------------------|
| GET | `/estudiante`                              | Lista todos los estudiantes, ordenados por apellido | `200 OK` |
| GET | `/estudiante/lu/{lu}`                      | Busca un estudiante por libreta universitaria | `200 OK` |
| GET | `/estudiante/genero/{genero}`              | Lista los estudiantes de un género | `200 OK` |
| GET | `/estudiante/carrera/{id}?ciudad={ciudad}` | Lista los estudiantes inscriptos a una carrera que viven en una ciudad | `200 OK` |
| POST | `/estudiante`                              | Da de alta un estudiante | `200 OK` |
| PUT | `/estudiante/{id}`                         | Modifica los datos de un estudiante | `200 OK` |
| DELETE | `/estudiante/{id}`                         | Elimina un estudiante | `204 No Content` |

#### Métodos GET

La ruta `/estudiante` devuelve la lista de todos los estudiantes, ordenados por apellido
de forma alfabética ascendente. Ejemplo de respuesta (recortada).

Se cumple la consigna 2.c

```json
[
    {
        "apellido": "Airy",
        "ciudad": "Dashtobod",
        "dni": "44782708",
        "edad": 27,
        "genero": "Male",
        "id": 49,
        "lu": "90958",
        "nombre": "Ham"
    }
]
```

#### Buscar por libreta universitaria (LU)

Para filtrar estudiantes por número de libreta universitaria se realiza la petición sobre la ruta
`/estudiante/lu/{lu}`.

Por ejemplo, para buscar al alumno con la libreta 90958, la ruta queda `/estudiante/lu/90958`.
Devuelve todos los datos del alumno. Cumple la consigna 2.d.

```json
{
    "apellido": "Airy",
    "ciudad": "Dashtobod",
    "dni": "44782708",
    "edad": 27,
    "genero": "Male",
    "id": 49,
    "lu": "90958",
    "nombre": "Ham"
}
```

Si el número de libreta no corresponde a ningún estudiante de la base de datos, se devuelve el
código de respuesta `404 Not Found`.

#### Buscar por género

Para filtrar estudiantes por género se realiza la petición sobre la ruta
`/estudiante/genero/{genero}`.

Los valores que existen en la base son `Male`,`Female`, `Polygender`, `Agender`, `Non-binary`,
`GenderFluid`, `Bigender`, `Masculino` y `Femenino`.

Por ejemplo, para buscar a los
estudiantes de género masculino, la ruta queda `/estudiante/genero/Male`. Devuelve la lista
de todos los estudiantes que coinciden con ese género. Cumple la consigna 2.e.

Ejemplo de respuesta (recortada, la lista real tiene más estudiantes):

```json
[
    {
        "apellido": "Airy",
        "ciudad": "Dashtobod",
        "dni": "44782708",
        "edad": 27,
        "genero": "Male",
        "id": 49,
        "lu": "90958",
        "nombre": "Ham"
    }
]
```

Si el género indicado no coincide con ningún estudiante, se devuelve una lista vacía (`[]`) con código `200 OK`.


#### Buscar por carrera y ciudad de residencia

Para obtener los estudiantes inscriptos a una carrera, filtrando además por ciudad de
residencia, se realiza la petición sobre la ruta `/estudiante/carrera/{id}?ciudad={ciudad}`,
donde `{id}` es el identificador de la carrera.

Por ejemplo, para buscar a los estudiantes de la carrera con id 10 que viven en Dashtobod, la
ruta queda `/estudiante/carrera/10?ciudad=Dashtobod`. Devuelve la lista de los estudiantes que
cumplen ambas condiciones. Cumple la consigna 2.g.

Ejemplo de respuesta (recortada, la lista real puede tener más estudiantes):

```json
[
    {
        "apellido": "Airy",
        "ciudad": "Dashtobod",
        "dni": "44782708",
        "edad": 27,
        "genero": "Male",
        "id": 49,
        "lu": "90958",
        "nombre": "Ham"
    }
]
```

Si ningún estudiante de esa carrera vive en la ciudad indicada, se devuelve una lista vacía
(`[]`) con código `200 OK`.


#### Metodo POST

Para dar de alta un estudiante se realiza una petición `POST` sobre la ruta `/estudiante`, enviando
los datos en el cuerpo de la petición en formato JSON. Cumple la consigna 2.a.

Ejemplo de cuerpo de la petición:

```json
{
    "dni": "47159785",
    "nombre": "Canelita",
    "apellido": "Daschund",
    "edad": 2,
    "ciudad": "Rauch",
    "lu": "250525"
}
```

Devuelve los datos del estudiante creado, incluyendo el `id` que le asignó la base de datos:

```json
{
    "id": 105,
    "dni": "47159785",
    "nombre": "Canelita",
    "apellido": "Daschund",
    "edad": 2,
    "genero": null,
    "ciudad": "Rauch",
    "lu": "250525"
}
```

No se permite repetir la libreta universitaria: si ya existe un estudiante con la misma `lu`,
el alta se rechaza. Devuelve el codigo `500 Internal Server Error`

#### Metodo PUT

Para modificar los datos de un estudiante existente se realiza una petición `PUT` sobre la ruta
`/estudiante/{id}`, donde `{id}` es el identificador del estudiante. Los datos nuevos se envían en
el cuerpo de la petición en formato JSON.

Por ejemplo, para modificar al estudiante con id 105, la ruta queda `/estudiante/105`. Ejemplo de
cuerpo de la petición:

```json
{
    "dni": "47159785",
    "nombre": "Canelita",
    "apellido": "Daschund",
    "edad": 3,
    "ciudad": "Rauch",
    "genero": "Female",
    "lu": "250525"
}
```

Devuelve los datos del estudiante con los cambios aplicados:

```json
{
    "id": 105,
    "dni": "47159785",
    "nombre": "Canelita",
    "apellido": "Daschund",
    "edad": 3,
    "genero": "Female",
    "ciudad": "Rauch",
    "lu": "250525"
}
```

Si no existe ningún estudiante con el `id` indicado, se devuelve el código de respuesta
`404 Not Found`.

#### Metodo DELETE

Para eliminar un estudiante se realiza una petición `DELETE` sobre la ruta `/estudiante/{id}`,
donde `{id}` es el identificador del estudiante.

Por ejemplo, para eliminar al estudiante con id 105, la ruta queda `/estudiante/105`. No requiere
cuerpo en la petición.

Si la eliminación es exitosa, se devuelve el código de respuesta `204 No Content`, sin cuerpo en
la respuesta.

Si no existe ningún estudiante con el `id` indicado, se devuelve el código de respuesta
`404 Not Found`.


### Entidad Carrera

Para realizar consultas sobre la entidad Carrera se utiliza la ruta
`http://localhost:8080/carrera`.

| Método | Ruta | Descripción | Respuesta exitosa |
|--------|------|-------------|-------------------|
| GET | `/carrera` | Lista todas las carreras | `200 OK` |
| GET | `/carrera/{id}` | Busca una carrera por id | `200 OK` |
| GET | `/carrera/inscriptos` | Lista las carreras con su cantidad de inscriptos | `200 OK` |
| GET | `/carrera/informe` | Informe de inscriptos y graduados por carrera y año | `200 OK` |
| POST | `/carrera` | Da de alta una carrera | `200 OK` |
| PUT | `/carrera/{id}` | Modifica una carrera | `200 OK` |
| DELETE | `/carrera/{id}` | Elimina una carrera | `204 No Content` |

#### Métodos GET

##### Listar todas las carreras

La ruta `/carrera` devuelve la lista de todas las carreras, ordenadas por id.

```json
[
    {
        "id": 1,
        "nombre": "TUDAI",
        "duracion": 2
    },
    {
        "id": 2,
        "nombre": "Abogacia",
        "duracion": 4
    }
]
```

##### Buscar una carrera por id

Para buscar una carrera por id se utiliza la ruta `/carrera/{id}`. Por ejemplo, para buscar la
carrera con id 5 se utiliza `/carrera/5`.

```json
{
    "id": 5,
    "nombre": "TUARI",
    "duracion": 2
}
```

Si el id no corresponde a ninguna carrera, se devuelve el código de respuesta `500`.

##### Obtener la cantidad de inscriptos

La ruta `/carrera/inscriptos` devuelve las carreras que tienen estudiantes inscriptos, ordenadas
por cantidad de inscriptos de mayor a menor. Las carreras sin inscriptos no aparecen en el
resultado. Cumple con la consigna 2.f.

```json
[
    {
        "nombre": "TUDAI",
        "duracion": 2,
        "cantInscriptos": 17
    },
    {
        "nombre": "Educacion Fisica",
        "duracion": 5,
        "cantInscriptos": 12
    }
]
```

##### Obtener el informe de carreras

La ruta `/carrera/informe` devuelve, para cada carrera, la cantidad de inscriptos y de graduados
de cada año. Las carreras se ordenan alfabéticamente por nombre y, dentro de cada una, los años
de forma cronológica. Si en un año hubo graduados pero no inscriptos (o al revés), el valor
correspondiente es 0. Cumple con la consigna 2.h.

```json
[
    {
        "nombre": "Abogacia",
        "duracion": 4,
        "anio": 2023,
        "inscriptos": 0,
        "graduados": 3
    },
    {
        "nombre": "Abogacia",
        "duracion": 4,
        "anio": 2024,
        "inscriptos": 0,
        "graduados": 1
    }
]
```

#### Método POST

Para cargar una nueva carrera se utiliza el método `POST` sobre `/carrera`. El cuerpo de la
petición espera únicamente `nombre` y `duracion`.

```json
{
    "nombre": "Psicologia",
    "duracion": 5
}
```

Devuelve la carrera creada, con su id.

```json
{
    "id": 16,
    "nombre": "Psicologia",
    "duracion": 5
}
```

#### Método PUT

Para actualizar una carrera se utiliza el método `PUT` sobre `/carrera/{id}`. Por ejemplo, para
modificar la carrera con id 16 se utiliza `/carrera/16`. Espera los mismos atributos que el
POST (`nombre` y `duracion`) y devuelve la carrera ya modificada.

```json
{
    "id": 16,
    "nombre": "Psicologia",
    "duracion": 6
}
```

#### Método DELETE

Para eliminar una carrera se utiliza el método `DELETE` sobre `/carrera/{id}`. Si la eliminación
es exitosa, devuelve el código `204 No Content`, sin cuerpo en la respuesta.

### Entidad Inscripcion

Para realizar consultas sobre la entidad Inscripcion se utiliza la ruta
`http://localhost:8080/inscripcion`. Una inscripción relaciona a un estudiante con una carrera y
guarda el año de inscripción, el año de graduación y la antigüedad.

| Método | Ruta | Descripción | Respuesta exitosa |
|--------|------|-------------|-------------------|
| GET | `/inscripcion` | Lista todas las inscripciones, ordenadas por carrera | `200 OK`          |
| GET | `/inscripcion/{id}` | Busca una inscripción por id | `200 OK`          |
| GET | `/inscripcion/estudiante/{id}` | Lista las inscripciones de un estudiante | `200 OK`          |
| GET | `/inscripcion/carrera/{id}` | Lista las inscripciones de una carrera | `200 OK`          |
| POST | `/inscripcion` | Inscribe a un estudiante en una carrera | `200 OK`          |
| PUT | `/inscripcion/{id}` | Modifica una inscripción | `200 OK`          |
| DELETE | `/inscripcion/{id}` | Elimina una inscripción | `200 OK`          |


#### Métodos GET

El 0 en graduación hace referencia a que el estudiante aún no se graduó.

##### Listar todas las inscripciones

La ruta `/inscripcion` devuelve la lista de todas las inscripciones, ordenadas por carrera.

```json
[
  {
    "antiguedad": 4,
    "carreraNombre": "TUDAI",
    "estudianteApellido": "Bayle",
    "estudianteDni": "84076286",
    "estudianteNombre": "Sebastien",
    "graduacion": 0,
    "id": 16,
    "inscripcion": 2022
  },
  {
    "antiguedad": 2,
    "carreraNombre": "TUDAI",
    "estudianteApellido": "Guymer",
    "estudianteDni": "66647912",
    "estudianteNombre": "Shep",
    "graduacion": 2023,
    "id": 25,
    "inscripcion": 2020
  }
]
```

##### Buscar una inscripción por id

La ruta `/inscripcion/{id}` devuelve la inscripción con ese identificador. Por ejemplo,
`/inscripcion/1` devuelve:

```json
{
  "antiguedad": 5,
  "carreraNombre": "Arte",
  "estudianteApellido": "Blackmuir",
  "estudianteDni": "71779527",
  "estudianteNombre": "Isidro",
  "graduacion": 2022,
  "id": 1,
  "inscripcion": 2017
}
```

##### Inscripciones de un estudiante

La ruta `/inscripcion/estudiante/{id}` devuelve todas las inscripciones del estudiante con ese
id. Por ejemplo, `/inscripcion/estudiante/49`.

```json
[
  {
    "antiguedad": 5,
    "carreraNombre": "Arte",
    "estudianteApellido": "Blackmuir",
    "estudianteDni": "71779527",
    "estudianteNombre": "Isidro",
    "graduacion": 2022,
    "id": 1,
    "inscripcion": 2017
  },
  {
    "antiguedad": 2,
    "carreraNombre": "Educacion Fisica",
    "estudianteApellido": "Blackmuir",
    "estudianteDni": "71779527",
    "estudianteNombre": "Isidro",
    "graduacion": 2022,
    "id": 7,
    "inscripcion": 2021
  }
]
```

##### Inscripciones de una carrera

La ruta `/inscripcion/carrera/{id}` devuelve todas las inscripciones de la carrera con ese id.
Por ejemplo, `/inscripcion/carrera/1`.

```json
[
  {
    "antiguedad": 4,
    "carreraNombre": "TUDAI",
    "estudianteApellido": "Bayle",
    "estudianteDni": "84076286",
    "estudianteNombre": "Sebastien",
    "graduacion": 0,
    "id": 16,
    "inscripcion": 2022
  },
  {
    "antiguedad": 2,
    "carreraNombre": "TUDAI",
    "estudianteApellido": "Guymer",
    "estudianteDni": "66647912",
    "estudianteNombre": "Shep",
    "graduacion": 2023,
    "id": 25,
    "inscripcion": 2020
  }
]
```

#### Método POST

Para inscribir a un estudiante en una carrera se utiliza el método `POST` sobre `/inscripcion`.
El cuerpo de la petición espera los siguientes atributos:

```json
{
  "estudianteId": 1,
  "carreraId": 10,
  "inscripcion": 2026,
  "graduacion": 0,
  "antiguedad": 0
}
```

Devuelve la inscripción creada.

```json
{
  "antiguedad": 0,
  "carreraNombre": "Licenciatura en Fisica",
  "estudianteApellido": "Blackmuir",
  "estudianteDni": "71779527",
  "estudianteNombre": "Isidro",
  "graduacion": 0,
  "id": 105,
  "inscripcion": 2026
}
```


No se permite inscribir dos veces al mismo estudiante en la misma
carrera. En caso de que la inscripcion ya exista, devuelve el codigo `500 Internal Server Error`

#### Método PUT

Para modificar una inscripción se utiliza el método `PUT` sobre `/inscripcion/{id}`. Por ejemplo,
`/inscripcion/1`. El cuerpo del request espera los siguientes atributos:

```json
{
    "estudianteId": 1,
    "carreraId": 10,
    "inscripcion": 2025,
    "graduacion": 0,
    "antiguedad": 1
}
```

Devuelve los datos ya actualizados

```json
{
  "antiguedad": 1,
  "carreraNombre": "Licenciatura en Fisica",
  "estudianteApellido": "Blackmuir",
  "estudianteDni": "71779527",
  "estudianteNombre": "Isidro",
  "graduacion": 0,
  "id": 105,
  "inscripcion": 2025
}
```

#### Método DELETE

Para eliminar una inscripción se utiliza el método `DELETE` sobre `/inscripcion/{id}`. Si la
eliminación es exitosa, devuelve el código `200 Ok`, sin cuerpo en la respuesta.