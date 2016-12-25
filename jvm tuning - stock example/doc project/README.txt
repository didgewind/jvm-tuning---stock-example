Para ejecutar cualquier app, abrir un terminal, navegar a la carpeta del proyecto
y ejecutar las clases de esta manera:

time java -cp bin:lib/eclipselink-2.5.0.jar:lib/hibernate-jpa-2.1-api-1.0.0.final.jar \
-DMockEntityManager=MockEntityManager net.sdo.StockPriceHistoryBatcher \
500 1/1/01 12/31/06 0 100 > /dev/null

En este caso estamos recuperando 500 stocks en el intervalo especificado con la implementación normal
(la otra opción, 1, sería de loggeo) y almacenamos 100 resultados en el heap.