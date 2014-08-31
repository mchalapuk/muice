= Inject with Efficiency! =
Muice is a clone of [http://code.google.com/p/google-guice/ Google Guice] dependency injection library. In comparison with the original, Muice is far more efficient at cost of limited functionality. Motivation behind Muice is that Guice is hardly applicable for use in mobile applications, which require fast start-up time.

== Differences from Guice ==
Most of [http://code.google.com/p/google-guice/wiki/Motivation?tm=6 Guice Wiki] applies also to Muice. Following Guice features are currently unsupported:

 * @Provides Methods
 * Built-in Logger Binding
 * Just-In-Time Bindings
 * Binding Annotations with Parameters (including @Named)
 * Stages
 * Eager Singletons
 * Method Injections
 * Field Injections
 * Optional Injections
 * Static Injections
 * @Nullable Injections
 * Aspect Oriented Programming
 * Elements SPI
 * All Guice integration mechanisms
 * All Guice extensions

Guice provides its own version of @Inject annotation and declares @!BindingAnnotation. javax.inject.Inject and javax.inject.Qualifier are used in Muice.

Internals of Muice are completely different than Guice internals.

Besides Guice functionality, Muice provides a possibility to customize injector creation process (see [http://code.google.com/p/muice/source/browse/muice/src/main/java/pl/chalapuk/muice/customization/MuiceBuilder.java MuiceBuilder]).

== Documentation ==

At this point documentation can be found in the source code only. !JavaDoc of [http://code.google.com/p/muice/source/browse/muice/src/main/java/pl/chalapuk/muice/Muice.java Muice] and [http://code.google.com/p/muice/source/browse/muice/src/main/java/pl/chalapuk/muice/Binder.java Binder] classes are good starting points. 

== Downloads ==

Project archives can be downloaded from [http://search.maven.org/#artifactdetails|pl.chalapuk.muice|muice|0.1|jar maven central repository].
