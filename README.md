Documentation site on Heroku
=====================================

Documentation may be written in markdown and served by a minimal webapp.  
Documentation kept in vcs, along with the project it pertains to, if any.  

## Implementation
Client asks for ´/doc/page.md´: respond with ´/doc/page.md´  
Client asks for ´/doc/page.html´: respond with an html page that render page.md inside.  
Rendering is done using JavaScript MarkDown renderer [PageDown][pd] or [ShowDown][sd] and [google-code-prettify][prettify] syntax highlighting  
Server side logic is done with [Play 2][play] [Scala][scala]


## Infrastructure

[Play2 on Heroku][p2h]  
[Heroku Scala Play2 Sample][p2hsample]  

heroku create  
git push heroku master  
play debug run  

##
I set up so that published projects (2my + antares docsite) have docengine upstream. See [GitRef on remotes][git-remote]. In order to manage passwords, I followed tip on [GitHub and Multiple Accounts][git-passwords].  

(antares.docsite) git pull upstream master  
(2my.docsite) git pull upstream master  
(2my.docsite) git push origin master  



[pd]: http://code.google.com/p/pagedown/wiki/PageDown
[sd]: https://github.com/coreyti/showdown
[prettify]: http://code.google.com/p/google-code-prettify/
[play]: http://www.playframework.org/
[scala]: http://www.scala-lang.org/

[p2h]: https://github.com/playframework/Play20/wiki/ProductionHeroku
[p2hsample]: https://github.com/heroku/scala-play-sample

[git-remote]: http://gitref.org/remotes/
[git-passwords]: http://net.tutsplus.com/tutorials/tools-and-tips/how-to-work-with-github-and-multiple-accounts/