[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Overview

If you use vim to work on scala projects that use sbt, then this might be for you.  The intended workflow is to have a terminal window running `sbt ~test` or `sbt ~compile` or `sbt ~scalastyle` or some such continuously while you use gvim/mvim in another window.  With this sbt plugin and its companion vim plugin, whenever errors are detected, the offending code will be highlighted inline and you can jump between compile errors or test case failures with simple vim commands (I use the [unimpaired][] plugin to jump around with `]l`, `]L`, `[l`, `[L`).  For me, this is incredibly handy.

Unlike other solutions of this nature, this one does not hang vim while some slow syntax checker is run or otherwise slow down your system in any way.  Instead, most recent output from sbt is captured to log files and then quickly slurped into vim by [syntastic][] using the custom syntax checker in the companion [vim-scala-async-integration][] vim plugin.

# Status

This is in some kind of alpha status as of Feb '16.  I'm using it, but there are some little quirks and minor bugs.  Despite that, I find it pretty usable.  But because of that I haven't packaged this up or announced it yet.

# Installation

Installation requires two parts.  The first involves installing the SBT plugin and the second involves installing the vim plugin.  For now, the SBT plugin is not packaged so you will need to manage it manually.

## SBT Plugin Install

```bash
> git clone git@github.com:zmre/sbt-vim-async-integration.git
> cd sbt-vim-async-integration
> sbt publishLocal
> mkdir -p ~/.sbt/0.13/plugins
> echo 'addSbtPlugin("zmre" % "sbt-vim-async-integration" % "1.0-LOCAL")' >> ~/.sbt/0.13/plugins/plugins.sbt
```

Note: the mkdir will fail if that directory already exists, which is fine.

## Vim Plugin Install

There are a ton of different ways to install vim plugins.  I'll cover vundle and pathogen and you can figure the rest out on your own.

### Vundle

Add the following two lines to your vimrc file:

```
Bundle 'scrooloose/syntastic'
Bundle 'zmre/vim-scala-async-integration'
```

and then fire up vim and run `:BundleInstall`.

### Pathogen

```bash
> cd ~/.vim/bundle
> git clone git@github.com:zmre/vim-scala-async-integration.git
```

# Credits

This has borrowed heavily from [sbt-quickfix][].





[unimpaired]: https://github.com/tpope/vim-unimpaired
[sbt-vim-async-integration]: https://github.com/zmre/sbt-vim-async-integration
[vim-scala-async-integration]: https://github.com/zmre/vim-scala-async-integration
[syntastic]: https://github.com/scrooloose/syntastic
[sbt-quickfix]: https://github.com/dscleaver/sbt-quickfix
[scalastyle]: http://www.scalastyle.org
