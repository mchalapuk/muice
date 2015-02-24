> # DISCLAIMER
> Although this library may perform better than Guice,
> I (author) strongly **DISCOURAGE ANY USAGE OF THIS LIBRARY**!
> 
> In most cases, [Guice](http://code.google.com/p/google-guice/)
> is the way to go. Startup performance problems in mobile apps
> should to be dealt with by applying lazy loading technique.
> Changing DI framework for performance-related reasons is a form
> of micro-optimization, which should be used as last resort
> in most extreme cases.
> 
> If micro-optimization is really needed, [Dagger](https://github.com/square/dagger)
> will may a better choice. It is actively developed and provides
> more features. Dagger presents better performance than Muice and Guice,
> because its injectors do not use Java reflection.
> 
> 
</div>

# Inject with Efficiency!

Muice is a clone of [Google Guice](http://code.google.com/p/google-guice/) dependency injection library. In comparison with the original, Muice is far more efficient at cost of limited functionality. Motivation behind Muice is that Guice is hardly applicable for use in mobile applications, which require fast start-up time.

## Differences from Guice

Most of [Guice Wiki](http://code.google.com/p/google-guice/wiki/Motivation?tm=6) applies also to Muice. Following Guice features are currently unsupported:

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

Guice provides its own version of @Inject annotation and declares @BindingAnnotation. javax.inject.Inject and javax.inject.Qualifier are used in Muice.

Internals of Muice are completely different than Guice internals.

Besides Guice functionality, Muice provides a possibility to customize injector creation process (see [MuiceBuilder](https://github.com/muroc/muice/blob/master/muice/src/main/java/pl/chalapuk/muice/customization/MuiceBuilder.java)).

## Documentation

Documentation can be found in the source code. !JavaDoc of [Muice](https://github.com/muroc/muice/blob/master/muice/src/main/java/pl/chalapuk/muice/Muice.java) and [Binder](https://github.com/muroc/muice/blob/master/muice/src/main/java/pl/chalapuk/muice/Binder.java) classes are good starting points. 

## Downloads

Project archives can be downloaded from [maven central repository](http://search.maven.org/#artifactdetails|pl.chalapuk.muice|muice|0.1|jar).
