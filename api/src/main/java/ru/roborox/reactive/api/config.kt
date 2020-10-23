package ru.roborox.reactive.api

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(excludeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, value = [Configuration::class])])
class RoboroxApiConfiguration

