bitmap-font
====

![Multi-lang bitmap font in Clojure/LWJGL][img-bitmap-font-demo]

**한국어**로 된 자세한 설명: <http://bakyeono.net/post/2013-12-03-clojure-hangul-bitmap-font.html>


This Clojure/LWJGL demo renders bitmap fonts in good old 1990s MS-DOS style.

Of course, there's nothing special in rendering bitmap fonts itself. But this demo supports [Hangul][wiki-hangul] bitmap fonts, whose printing system is somewhat difficult. It isn't such a no-brainer as bliting Latin Alphabet bitmap fonts, for, in Hangul, 2-3 parts('jamo's) combine into a full character.

I'm also planning to support other languages if I can get other bitmap fonts whose characters have height of 16 pixel.

## Usage

You can run the demo by typing this on command line:

~~~
lein run
~~~

## License

Copyright © 2013 http://www.bakyeono.net

Distributed under the Eclipse Public License either version 1.0 or any later version.

[wiki-hangul]: http://en.wikipedia.org/wiki/Hangul

[img-bitmap-font-demo]: doc/img/bitmap-font-demo.png
