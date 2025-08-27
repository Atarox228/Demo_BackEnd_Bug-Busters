## 📖 Propósito del proyecto
Este repositorio corresponde a un **proyecto académico institucional** desarrollado en el marco de la carrera de Tecnico en Programacion Universitaria/Licenciatura en Informatioca de la Universidad Nacional de Quilmes (UNQ).  
El objetivo principal fue aplicar en un caso práctico los conocimientos adquiridos en la materia [Estrategias de Persistencia], trabajando en equipo para resolver un problema propuesto por el cuerpo docente.

## 🛠️ Aptitudes desarrolladas
Durante el desarrollo del proyecto se trabajó en:
- **Colaboración en equipo** mediante Git y GitHub.
- **Diseño y desarrollo backend** en bases de datos SQL y NoSQL.
- **Buenas prácticas de programación** (patrones, testing, clean code).
- **Gestión de versiones y ramas** en un flujo de trabajo compartido, gitflow.
- **Resolución de problemas** en un entorno simulado de proyecto real.

## 🤝 Aclaración
Este proyecto fue realizado en **conjunto con compañeros estudiantes**, como parte de un trabajo práctico institucional.  
Su finalidad principal es **educativa** y **demostrativa**, para exponer las habilidades técnicas y de colaboración desarrolladas durante el cursado.


> _Otro viernes en la facu, nada raro, no?_
> _Seis pibes de informatica y un ritual sin razon._
> 
> _Con sal velas y humo nos conectamos al mas alla..._
> _Y ahora las memorias del infierno nos vuelven a golpear._

Lo que en principio fue un viernes más en la universidad derivó, de alguna manera, en un pseudo-ritual en el cual los seis integrantes conectaron momentáneamente con seres de otros mundos, algunos de aspecto etéreo, otros antropomórficos pero con claras características ajenas a los humanos.

Tras el corto estado eufórico de esa experiencia, los seis programadores volvieron en sí, sentados en el living de la casa, atónitos ante lo visto.

Miradas desconcertadas volaron entre ellos durante unos minutos, pero todos comprendieron que ahora tenían una misión: representar a estos espectros en un modelo computable.

<p align="center">
  <img src="enunciado/epersgeist.png"/>
</p>

## Entregas
- [Entrega 1 - JDBC](enunciado/entrega1/entrega1.md)
- [Entrega 2 - ORM - Hibernate](enunciado/entrega2/entrega2.md)
- [Entrega 3 - ORM - Spring](/enunciado/entrega3/enunciado_tp3.md)
- [Entrega 4 - NoSQL - Neo4j - Spring](enunciado/entrega4/enunciado_tp4.md)
- [Entrega 5 - NoSQL - MongoDB - Spring](/enunciado/entrega5/enunciado_tp5.md)

## Bases de datos usadas
- SQL
  - PostgreSQL (SQL)
- NoSQL
  - Neo4J 
  - MongoDB 
  - Redis (Standalone)

## Implementaciones Agregadas
-En applicacition.properties: 
-
  - spring.mvc.throw-exception-if-no-handler-found=true
  - spring.web.resources.add-mappings=false

  - Sirven para el control preciso de rutas inexistentes, esto permite hacer un Handleo de error en vez de tirar un 404 cuando la ruta esta equivocada. Lo usamos para tener mayor precision de los errores pero esto hace que los archivos estaticos manejados de manera automatica por Spring sea desactivado. Se eligio usar debido a que por el momento no estamos usando frontend y, por lo tanto, no necesitamos rutas para archivos estáticos.
  
-En build.gradle:
-
  - implementation 'jakarta.validation:jakarta.validation-api:3.0.2'
  - implementation 'org.hibernate.validator:hibernate-validator:8.0.1.Final'

  - Mientras que la primera nos permite agregar validaciones como @Max, @Min para nuestros atributos y @Valid para validar los cuerpos pasados por HTTP. El segundo permite realizar las validaciones durante el tiempo de ejecucion, permitiendo que se ejecuten validaciones automaticas para las consultas REST. En sintesis, el primero es el API que nos da los recursos(anotaciones) mientras que el segundo es quien nos permite implementar dichos recursos(validaciones) 

