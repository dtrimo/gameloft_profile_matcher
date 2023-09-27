# gameloft_profile_matcher

The application uses an in-memory h2 database to store campaigns and user profiles.
The data is generated via flyway scripts (xmls), in order to make both the ddl and dml statements database independent.

I made separate classes for mapping to the database entities and for presenting the
entities in the views (via rest controllers) in order to separate concerns. I strongly
prefer to keep the database model (annotated with persistence annotations) separate
from the view model (usually annotated with Jackson annotations).

Controllers are annotated with @Transactional in order to allow the lazy loading
of associated entities (like items and clans) during the mapping process.

I changed the syntax of the matcher json a little, in order to support a wider range of conditions to be expressed.

The implementation aims to support the following use cases:
- Allow specifying multiple conditions that a profile must meet simultaneously (logical AND). Each
property that is checked can be checked for an exact match (ex: "xp" : 1000) or for arithmetical tests
  (ex: "xp" : {"min" : 100, "max" : "5000"}).
- Allow specifying multiple conditions, at least one of which must be satisfied (logical OR)
- Be independent of domain knowledge (the matchers do not depend on any specifics of profiles and campaigns)