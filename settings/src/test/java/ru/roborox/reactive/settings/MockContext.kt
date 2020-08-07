package ru.roborox.reactive.settings

import ru.roborox.reactive.persist.configuration.EnableRoboroxMongo
import ru.roborox.reactive.persist.configuration.IncludePersistProperties
import ru.roborox.reactive.settings.configuration.EnableRoboroxSettings

@EnableRoboroxSettings
@EnableRoboroxMongo
@IncludePersistProperties
class MockContext