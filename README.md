# ClojureScript Front-end Demos

Here are self-contained ClojureScript front-end demos including:
- Reagent and re-frame for state handling,
- styling using CSS, reusable components and utility classes,
- useful greppable naming conventions and
- browser location and history management.

Future may include:
- iteration on the patterns,
- documentation practices,
- component guide,
- backend integration,
- testing on different levels,
- progressive loading of content,
- offline support,
- distributed data handling,
- responsive design and
- SSR, PWA, A11Y, L18N…

Contact [Markku Rontu](http://markku.rontu.net/)


## Setup

To get an interactive development environment run:

    lein figwheel


and open your browser at [localhost:3450](http://localhost:3450/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © 2020 Markku Rontu

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
