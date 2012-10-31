Documentation site on Heroku
=====================================

Documentation may be written in markdown and served by a minimal webapp.
Documentation kept in vcs, along with the project it pertains to, if any.

## Implementation
Client asks for ´/doc/page.md´: respond with ´/doc/page.md´
Client asks for ´/doc/page.html´: respond with an html page that render page.md inside.
Rendering is done using JavaScript MarkDown renderer [PageDown][1] or [ShowDown][2] and [google-code-prettify][3] syntax highlighting
Server side logic is done with [Play 2][4] [Scala][5]

[1]: http://code.google.com/p/pagedown/wiki/PageDown
[2]: https://github.com/coreyti/showdown
[3]: http://code.google.com/p/google-code-prettify/
[4]: http://www.playframework.org/
[5]: http://www.scala-lang.org/


## Infrastructure

[Play2 on Heroku][10]
[Heroku Scala Play2 Sample][11]

[10]: https://github.com/playframework/Play20/wiki/ProductionHeroku
[11]: https://github.com/heroku/scala-play-sample

heroku create
git push heroku master

http://fathomless-beach-8371.herokuapp.com/
http://fathomless-forest-7582.herokuapp.com/ | git@heroku.com:fathomless-forest-7582.git


