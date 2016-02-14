[sbt-vim-async-integration][] writes various warnings and errors from sbt's test, compile, [scalastyle][], lint:compile and other checks to a quickfix list and then sends a message to vim telling [syntastic][] to run. This sbt plugin pairs with a vim plugin called [vim-scala-async-integration][] that extends syntastic to bring some nice mechanics for navigating issues and seeing them inside vim.  In this mode, syntastic is asynchronous and does not spend time waiting for slow commands (such as compilers) to run.

This has borrowed heavily from [sbt-quickfix][].

[sbt-vim-async-integration]: https://github.com/zmre/sbt-vim-async-integration
[vim-scala-async-integration]: https://github.com/zmre/vim-scala-async-integration
[syntastic]: https://github.com/scrooloose/syntastic
[sbt-quickfix]: https://github.com/dscleaver/sbt-quickfix
[scalastyle]: http://www.scalastyle.org
