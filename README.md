[sbt-vim-async-integration][] writes various warnings and errors from sbt's test, compile, [scalastyle][] and other checks to a quickfix list and then sends a message to vim telling [syntastic][] to run. This sbt plugin pairs with a vim plugin called [vim-scala-async-integration][] that extends syntastic with a syntax checker that reads the error logs produced by scala and uses them to populate the quickfix list.  In this mode, syntastic is essentially asynchronous and does not spend time waiting for slow commands (such as compilers) to run.

This has borrowed heavily from [sbt-quickfix][].

[sbt-vim-async-integration]: https://github.com/zmre/sbt-vim-async-integration
[vim-scala-async-integration]: https://github.com/zmre/vim-scala-async-integration
[syntastic]: https://github.com/scrooloose/syntastic
[sbt-quickfix]: https://github.com/dscleaver/sbt-quickfix
[scalastyle]: http://www.scalastyle.org
