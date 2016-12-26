Proyecto de prueba para comprobar varias optimizaciones sobre la jvm. Ahora mismo hay una app monopuesto
y otra web.

Voy a explicar primero cómo ejecutar ambas apps y luego explico cómo monitorizarlas y probar diferentes
optimizaciones.

APPS
====

* Para ejecutar stock batching, abrir un terminal, navegar a la carpeta del proyecto
y ejecutar las clases de esta manera:

time java -cp bin:lib/eclipselink-2.5.0.jar:lib/hibernate-jpa-2.1-api-1.0.0.final.jar \
-DMockEntityManager=MockEntityManager net.sdo.StockPriceHistoryBatcher \
5000 1/1/01 12/31/06 0 100 > /dev/null

En este caso estamos recuperando 500 stocks en el intervalo especificado con la implementación normal
(valor 0, la otra opción, 1, sería de loggeo) y almacenamos 100 resultados en el heap. El time inicial
es una app de linux que me da el tiempo total de ejecución (user, sys y real)

* Para ejecutar la app web, invoco desde el navegador

	http://localhost:8080/stocks/stockservlet
	
Esto realiza una petición al servidor, y da una respuesta sobre el histórico de un stock aleatorio con valores por
defecto. Para analizar la carga del servidor y hacer pruebas, es mejor utilizar una app de prueba de carga, como
JMeter.

Los parámetros de la app web son los siguientes:

- symbol: stock a analizar

- startDate: fecha inicial en formato 01/01/01 (mes/día/año)

- endDate: fecha inicial en formato 01/01/01 (mes/día/año)

- log: true / false si queremos logging (¿cómo afecta al performance?

- save: entero indicando el número de elementos stockHistory que hay que almacenar en el heap.

- saveSoft / saveWeak: almacena los stockHistorys en uno u otro, pero no sé la diferencia.


Herramientas de monitorización y pruebas (hay más info en el doc apuntes jvm opt):
=================================================================================

- jvisualvm: lo arrancamos desde consola y conectamos al proceso java que queramos. Con el plugin Visualgc
(se instala desde tools - plugins) podemos monitorizar en tiempo real todas las generaciones del heap. Para
que funcione Visualgc hay que arrancar jvisualvm y tomcat con el mismo usuario (yo arranco los dos como made
modificando el usuario y el grupo en /etc/default/tomcat8).

Aquí podemos ver para cada generación el tamaño máximo, el tamaño actual y el espacio ocupado. También podemos
usarlo como profiler (sampler o instrumentado (profiler)). Es mejor poner el refresh rate a 100ms para ver cómo
cambia todo en tiempo real.

- vmstat: me da info diversa sobre recursos del so (uso de memoria, cpu, etc.). Para un ejemplo de su uso,
ver apuntes jvm opt.

