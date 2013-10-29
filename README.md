# bitmap-font

This Clojure library renders bitmap fonts in good old 1990s MS-DOS style.

Of course, there's nothing special in rendering bitmap font itself. But this library supports [Hangul][wiki-hangul] bitmap fonts, whose printing system is somewhat difficult. It isn't such a no-brainer as bliting Latin Alphabet bitmap fonts, for, in Hangul, 2 ~ 3 partial letters combine into a full character. (And they have diverse forms and do transforms...)

I'm also planning to support other languages if I can get other bitmap fonts (whose characters have this resolution: 16px x 16px),

I almost forgot this. I coded it on LWJGL. If you ever want to use it somewhere else, you may need to fix a few codes.

## Usage

STILL IN DEVELOPMENT!

## License

Copyright © 2013 http://www.bakyeono.net

Distributed under the Eclipse Public License either version 1.0 or any later version.

[wiki-hangul]: http://en.wikipedia.org/wiki/Hangul

