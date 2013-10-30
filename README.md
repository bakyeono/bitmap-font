# bitmap-font

![Multi-lang bitmap font in Clojure/LWJGL][img-bitmap-font-demo]

This Clojure/LWJGL demo renders bitmap fonts in good old 1990s MS-DOS style.

Of course, there's nothing special in rendering bitmap fonts itself. But this demo supports [Hangul][wiki-hangul] bitmap fonts, whose printing system is somewhat difficult. It isn't such a no-brainer as bliting Latin Alphabet bitmap fonts, for, in Hangul, 2 ~ 3 partial letters combine into a full character. (And they have diverse forms and do transforms...)

I'm also planning to support other languages if I can get other bitmap fonts whose characters have 16 x 16 resolution. (unit: pixel)

I coded it on LWJGL. If you ever want to use it somewhere else, you may need to fix a few lines (mainly bliting things).

## Usage

You can run the demo by following this steps:

1. lein repl

2. (-start)

## License

Copyright © 2013 http://www.bakyeono.net

Distributed under the Eclipse Public License either version 1.0 or any later version.

[wiki-hangul]: http://en.wikipedia.org/wiki/Hangul

[img-bitmap-font-demo]: doc/img/bitmap-font-demo.png
