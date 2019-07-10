<h2 align="center">Вывод по работе</h2> 
Исходя из полученных данных, "качетсво работы" GC G1 в большинстве случаев значительно превосходит остальные рассмотренные GC, по таким параметрам как пропускная способность и минимальное время отклика. Вариация параметра MaxGCPauseMillis позволяет добиться необхидимых для конекретной задачи показателей отклика и производительности. Низкое значение данного параметра позволяет получить минимальное время отклика, при этом оставаясь производительнее GC CMS. В случае, когда время отклика системы не является важным фактором, увеличение значения данного поараметра позволяет снизить суммарное время работы GC и повысить производительность приложения. Такие результаты скорее всего объясняются тем что часть работы GC выполняется в фоне без отсановки осталных потоков приложения, а также в несколько потоков.

Сравнивая Serial, Parallel и CMS можно сделать следующие выводы, на малом размере heap=128Mb лучшие показали производительности были получены для SerialGC и ParallelGc. Для HeapSize=512 и 1024 лучшие показатели производительности были полученены для ParallelGC. Таким образом можно сделать вывод, что в случае отсутствия выбора G1, для достижения максимальной производительности приложения слудет выбирать ParallelGC, в случае когда необходимо сохранить минимальное время отклика системы необходимос использовать CMS, в случае когда ресурсы существенно ограничены лучие результыты дает seriall GC

<h2 align="center">HeapSize=128MB</h2> 
<h4 align="center">

![](https://github.com/EkimenkoAnton/otus_java_2019/blob/hw05-gc/hw05-gc/src/main/resources/Stat128.png)
<br>Зависиость времени сборки GC в минуту от общего времени работы приложения
![](https://github.com/EkimenkoAnton/otus_java_2019/blob/hw05-gc/hw05-gc/src/main/resources/GCFuncInMinute128.png)
</h4> 

<h2 align="center">HeapSize=512MB</h2> 
<h4 align="center">

![](https://github.com/EkimenkoAnton/otus_java_2019/blob/hw05-gc/hw05-gc/src/main/resources/Stat512.png)
<br>Зависиость времени сборки GC в минуту от общего времени работы приложения
![](https://github.com/EkimenkoAnton/otus_java_2019/blob/hw05-gc/hw05-gc/src/main/resources/GCFuncInMinute512.png)
</h4> 

<h2 align="center">HeapSize=1024MB</h2> 
<h4 align="center">

![](https://github.com/EkimenkoAnton/otus_java_2019/blob/hw05-gc/hw05-gc/src/main/resources/Stat1024.png)
<br>Зависиость времени сборки GC в минуту от общего времени работы приложения
![](https://github.com/EkimenkoAnton/otus_java_2019/blob/hw05-gc/hw05-gc/src/main/resources/GCFuncInMinute1024.png)
</h4> 
