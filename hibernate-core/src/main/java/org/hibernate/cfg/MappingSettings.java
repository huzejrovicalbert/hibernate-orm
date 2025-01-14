/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.cfg;

import org.hibernate.Incubating;
import org.hibernate.Remove;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.ListIndexBase;
import org.hibernate.annotations.Nationalized;
import org.hibernate.boot.jaxb.hbm.transform.UnsupportedFeatureHandling;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.enhanced.ImplicitDatabaseObjectNamingStrategy;
import org.hibernate.id.enhanced.StandardOptimizerDescriptor;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.type.WrapperArrayHandling;
import org.hibernate.type.format.FormatMapper;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;

/**
 * @author Steve Ebersole
 */
public interface MappingSettings {
	/**
	 * A default database catalog name to use for unqualified database
	 * object (table, sequence, ...) names
	 *
	 * @see org.hibernate.boot.MetadataBuilder#applyImplicitCatalogName
	 */
	String DEFAULT_CATALOG = "hibernate.default_catalog";

	/**
	 * A default database schema (owner) name to use for unqualified
	 * database object (table, sequence, ...) names
	 *
	 * @see org.hibernate.boot.MetadataBuilder#applyImplicitSchemaName
	 */
	String DEFAULT_SCHEMA = "hibernate.default_schema";

	/**
	 * Setting that indicates whether to build the JPA types, either:<ul>
	 *     <li>
	 *         <b>enabled</b> - Do the build
	 *     </li>
	 *     <li>
	 *         <b>disabled</b> - Do not do the build
	 *     </li>
	 *     <li>
	 *         <b>ignoreUnsupported</b> - Do the build, but ignore any non-JPA
	 *         features that would otherwise result in a failure.
	 *     </li>
	 * </ul>
	 */
	String JPA_METAMODEL_POPULATION = "hibernate.jpa.metamodel.population";

	/**
	 * Setting that controls whether we seek out JPA "static metamodel" classes
	 * and populate them, either:<ul>
	 *     <li>
	 *         <b>enabled</b> - Do the population
	 *     </li>
	 *     <li>
	 *         <b>disabled</b> - Do not do the population
	 *     </li>
	 *     <li>
	 *         <b>skipUnsupported</b> - Do the population, but ignore any non-JPA
	 *         features that would otherwise result in the population failing.
	 *     </li>
	 * </ul>
	 */
	String STATIC_METAMODEL_POPULATION = "hibernate.jpa.static_metamodel.population";

	/**
	 * When enabled, all database identifiers are quoted.
	 * <p>
	 * Corollary to the JPA {@code <delimited-identifiers/>} element within
	 * the {@code orm.xml} {@code <persistence-unit-defaults/>} element, but
	 * offered as a global flag.
	 *
	 * @settingDefault {@code false}
	 */
	String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";

	/**
	 * Controls whether column-definitions ({@link Column#columnDefinition},
	 * {@link JoinColumn#columnDefinition}, etc.) should be auto-quoted as part of
	 * {@linkplain #GLOBALLY_QUOTED_IDENTIFIERS global quoting}.
	 * <p>
	 * When {@linkplain #GLOBALLY_QUOTED_IDENTIFIERS global quoting} is enabled, JPA
	 * <a href="https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.html#a988">states</a>
	 * that column-definitions are subject to quoting.  However, this can lead to problems
	 * with definitions such as {@code @Column(..., columnDefinition="INTEGER DEFAULT 20")}.
	 *
	 * @settingDefault {@code false} to avoid the potential problems quoting non-trivial
	 * column-definitions.
	 */
	String GLOBALLY_QUOTED_IDENTIFIERS_SKIP_COLUMN_DEFINITIONS = "hibernate.globally_quoted_identifiers_skip_column_definitions";

	/**
	 * Specifies whether to automatically quote any names that are deemed keywords
	 * on the underlying database.
	 *
	 * @settingDefault {@code false} - auto-quoting of SQL keywords is disabled by default.
	 *
	 * @since 5.0
	 */
	String KEYWORD_AUTO_QUOTING_ENABLED = "hibernate.auto_quote_keyword";

	/**
	 * When a generator specifies an increment-size and an optimizer was not explicitly
	 * specified, which of the "pooled" optimizers should be preferred? Can specify an
	 * optimizer short name or the name of a class which implements
	 * {@link org.hibernate.id.enhanced.Optimizer}.
	 *
	 * @settingDefault {@link StandardOptimizerDescriptor#POOLED}
	 */
	String PREFERRED_POOLED_OPTIMIZER = "hibernate.id.optimizer.pooled.preferred";

	/**
	 * Determines if the identifier value stored in the database table backing a
	 * {@linkplain jakarta.persistence.TableGenerator table generator} is the last
	 * value returned by the identifier generator, or the next value to be returned.
	 *
	 * @settingDefault The value stored in the database table is the last generated value
	 *
	 * @since 5.3
	 */
	String TABLE_GENERATOR_STORE_LAST_USED = "hibernate.id.generator.stored_last_used";

	/**
	 * This setting defines the {@link org.hibernate.id.SequenceMismatchStrategy} used
	 * when Hibernate detects a mismatch between a sequence configuration in an entity
	 * mapping and its database sequence object counterpart.
	 * <p>
	 * Possible values are {@link org.hibernate.id.SequenceMismatchStrategy#EXCEPTION},
	 * {@link org.hibernate.id.SequenceMismatchStrategy#LOG},
	 * {@link org.hibernate.id.SequenceMismatchStrategy#FIX}
	 * and {@link org.hibernate.id.SequenceMismatchStrategy#NONE}.
	 *
	 * @settingDefault {@link org.hibernate.id.SequenceMismatchStrategy#EXCEPTION},  meaning
	 * that an exception is thrown when such a conflict is detected.
	 *
	 * @since 5.4
	 */
	String SEQUENCE_INCREMENT_SIZE_MISMATCH_STRATEGY = "hibernate.id.sequence.increment_size_mismatch_strategy";

	/**
	 * Specifies the preferred JDBC type for storing boolean values.
	 * <p>
	 * Can be overridden locally using {@link org.hibernate.annotations.JdbcType},
	 * {@link org.hibernate.annotations.JdbcTypeCode}, and friends.
	 * <p>
	 * Can also specify the name of the {@link org.hibernate.type.SqlTypes} constant
	 * field, for example, {@code hibernate.type.preferred_boolean_jdbc_type=BIT}.
	 *
	 * @settingDefault {@linkplain Dialect#getPreferredSqlTypeCodeForBoolean dialect-specific type code}
	 *
	 * @since 6.0
	 */
	@Incubating
	String PREFERRED_BOOLEAN_JDBC_TYPE = "hibernate.type.preferred_boolean_jdbc_type";

	/**
	 * The preferred JDBC type to use for storing {@link java.util.UUID} values.
	 * <p>
	 * Can be overridden locally using {@link org.hibernate.annotations.JdbcType},
	 * {@link org.hibernate.annotations.JdbcTypeCode}, and friends.
	 * <p>
	 * Can also specify the name of the {@link org.hibernate.type.SqlTypes} constant
	 * field, for example, {@code hibernate.type.preferred_uuid_jdbc_type=CHAR}.
	 *
	 * @settingDefault {@link org.hibernate.type.SqlTypes#UUID}.
	 *
	 * @since 6.0
	 */
	@Incubating
	String PREFERRED_UUID_JDBC_TYPE = "hibernate.type.preferred_uuid_jdbc_type";

	/**
	 * The preferred JDBC type to use for storing {@link java.time.Duration} values.
	 * <p>
	 * Can be overridden locally using {@link org.hibernate.annotations.JdbcType},
	 * {@link org.hibernate.annotations.JdbcTypeCode}, and friends.
	 * <p>
	 * Can also specify the name of the {@link org.hibernate.type.SqlTypes} constant
	 * field, for example, {@code hibernate.type.preferred_duration_jdbc_type=INTERVAL_SECOND}.
	 *
	 * @settingDefault {@link org.hibernate.type.SqlTypes#NUMERIC}
	 *
	 * @since 6.0
	 */
	@Incubating
	String PREFERRED_DURATION_JDBC_TYPE = "hibernate.type.preferred_duration_jdbc_type";

	/**
	 * Specifies the preferred JDBC type for storing {@link java.time.Instant} values.
	 * <p>
	 * Can be overridden locally using {@link org.hibernate.annotations.JdbcType},
	 * {@link org.hibernate.annotations.JdbcTypeCode}, and friends.
	 * <p>
	 * Can also specify the name of the {@link org.hibernate.type.SqlTypes} constant
	 * field, for example, {@code hibernate.type.preferred_instant_jdbc_type=TIMESTAMP}.
	 *
	 * @settingDefault {@link org.hibernate.type.SqlTypes#TIMESTAMP_UTC}.
	 *
	 * @since 6.0
	 */
	@Incubating
	String PREFERRED_INSTANT_JDBC_TYPE = "hibernate.type.preferred_instant_jdbc_type";

	/**
	 * Specifies a {@link org.hibernate.type.format.FormatMapper} used for JSON
	 * serialization and deserialization, either:
	 * <ul>
	 *     <li>an instance of {@code FormatMapper},
	 *     <li>a {@link Class} representing a class that implements {@code FormatMapper},
	 *     <li>the name of a class that implements {@code FormatMapper}, or
	 *     <li>one of the shorthand constants {@code jackson} or {@code jsonb}.
	 * </ul>
	 * <p>
	 * By default, the first of the possible providers that is available at runtime is
	 * used, according to the listing order.
	 *
	 * @since 6.0
	 * @see org.hibernate.boot.SessionFactoryBuilder#applyJsonFormatMapper(FormatMapper)
	 */
	@Incubating
	String JSON_FORMAT_MAPPER = "hibernate.type.json_format_mapper";

	/**
	 * Specifies a {@link org.hibernate.type.format.FormatMapper} used for XML
	 * serialization and deserialization, either:
	 * <ul>
	 *     <li>an instance of {@code FormatMapper},
	 *     <li>a {@link Class} representing a class that implements {@code FormatMapper},
	 *     <li>the name of a class that implements {@code FormatMapper}, or
	 *     <li>one of the shorthand constants {@code jackson} or {@code jaxb}.
	 * </ul>
	 * <p>
	 * By default, the first of the possible providers that is available at runtime is
	 * used, according to the listing order.
	 *
	 * @since 6.0.1
	 * @see org.hibernate.boot.SessionFactoryBuilder#applyXmlFormatMapper(FormatMapper)
	 */
	@Incubating
	String XML_FORMAT_MAPPER = "hibernate.type.xml_format_mapper";

	/**
	 * Configurable control over how to handle {@code Byte[]} and {@code Character[]} types
	 * encountered in the application domain model.  Allowable semantics are defined by
	 * {@link WrapperArrayHandling}.  Accepted values include:<ol>
	 *     <li>{@link WrapperArrayHandling} instance</li>
	 *     <li>case-insensitive name of a {@link WrapperArrayHandling} instance (e.g. {@code allow})</li>
	 * </ol>
	 *
	 * @since 6.2
	 */
	@Incubating
	String WRAPPER_ARRAY_HANDLING = "hibernate.type.wrapper_array_handling";

	/**
	 * Specifies the default strategy for storage of the timezone information for the zoned
	 * datetime types {@link java.time.OffsetDateTime} and {@link java.time.ZonedDateTime}.
	 * The possible options for this setting are enumerated by
	 * {@link org.hibernate.annotations.TimeZoneStorageType}.
	 *
	 * @apiNote For backward compatibility with older versions of Hibernate, set this property to
	 * {@link org.hibernate.annotations.TimeZoneStorageType#NORMALIZE NORMALIZE}.
	 *
	 * @settingDefault {@link org.hibernate.annotations.TimeZoneStorageType#DEFAULT DEFAULT},
	 * which guarantees that the {@linkplain java.time.OffsetDateTime#toInstant() instant}
	 * represented by a zoned datetime type is preserved by a round trip to the database.
	 * It does <em>not</em> guarantee that the time zone or offset is preserved.
	 *
	 * @see org.hibernate.annotations.TimeZoneStorageType
	 * @see org.hibernate.annotations.TimeZoneStorage
	 *
	 * @since 6.0
	 */
	String TIMEZONE_DEFAULT_STORAGE = "hibernate.timezone.default_storage";

	/**
	 * Used to specify the {@link org.hibernate.boot.model.naming.ImplicitNamingStrategy}
	 * class to use. The following shortcut names are defined for this setting:
	 * <ul>
	 *     <li>{@code "default"} and {@code "jpa"} are an abbreviations for
	 *     {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl}
	 *     <li>{@code "legacy-jpa"} is an abbreviation for
	 *     {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl}
	 *     <li>{@code "legacy-hbm"} is an abbreviation for
	 *     {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl}
	 *     <li>{@code "component-path"} is an abbreviation for
	 *     {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl}
	 * </ul>
	 *
	 * @settingDefault {@code "default"}
	 *
	 * @see org.hibernate.boot.MetadataBuilder#applyImplicitNamingStrategy
	 *
	 * @since 5.0
	 */
	String IMPLICIT_NAMING_STRATEGY = "hibernate.implicit_naming_strategy";

	/**
	 * Specifies the {@link org.hibernate.boot.model.naming.PhysicalNamingStrategy} to use.
	 *
	 * @settingDefault {@link org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl},
	 * in which case physical names are taken to be identical to logical names.
	 *
	 * @see org.hibernate.boot.MetadataBuilder#applyPhysicalNamingStrategy
	 *
	 * @since 5.0
	 */
	String PHYSICAL_NAMING_STRATEGY = "hibernate.physical_naming_strategy";

	/**
	 * An implicit naming strategy for database structures (tables, sequences) related
	 * to identifier generators.
	 * <p>
	 * Resolution uses the {@link org.hibernate.boot.registry.selector.spi.StrategySelector}
	 * service and accepts any of the forms discussed on
	 * {@link StrategySelector#resolveDefaultableStrategy(Class, Object, java.util.concurrent.Callable)}.
	 * <p>
	 * The recognized short names being:<ul>
	 *     <li>{@value org.hibernate.id.enhanced.SingleNamingStrategy#STRATEGY_NAME}</li>
	 *     <li>{@value org.hibernate.id.enhanced.LegacyNamingStrategy#STRATEGY_NAME}</li>
	 *     <li>{@value org.hibernate.id.enhanced.StandardNamingStrategy#STRATEGY_NAME}</li>
	 * </ul>
	 *
	 * @settingDefault {@link org.hibernate.id.enhanced.StandardNamingStrategy}
	 *
	 * @since 6
	 *
	 * @see ImplicitDatabaseObjectNamingStrategy
	 */
	@Incubating
	String ID_DB_STRUCTURE_NAMING_STRATEGY = "hibernate.id.db_structure_naming_strategy";

	/**
	 * Used to specify the {@link org.hibernate.boot.model.relational.ColumnOrderingStrategy}
	 * class to use. The following shortcut names are defined for this setting:
	 * <ul>
	 *     <li>{@code "default"} is an abbreviations for
	 *     {@link org.hibernate.boot.model.relational.ColumnOrderingStrategyStandard}
	 *     <li>{@code "legacy"} is an abbreviation for
	 *     {@link org.hibernate.boot.model.relational.ColumnOrderingStrategyLegacy}
	 * </ul>
	 *
	 * @settingDefault {@code "default"}
	 *
	 * @see org.hibernate.boot.MetadataBuilder#applyColumnOrderingStrategy
	 *
	 * @since 6.2
	 */
	String COLUMN_ORDERING_STRATEGY = "hibernate.column_ordering_strategy";

	/**
	 * Specifies the order in which metadata sources should be processed, is a delimited list
	 * of values defined by {@link MetadataSourceType}.
	 *
	 * @settingDefault {@code "hbm,class"}, which indicates that {@code hbm.xml} files
	 * should be processed first, followed by annotations and {@code orm.xml} files.
	 *
	 * @see MetadataSourceType
	 * @see org.hibernate.boot.MetadataBuilder#applySourceProcessOrdering(MetadataSourceType...)
	 *
	 * @deprecated {@code hbm.xml} mappings are no longer supported, making this attribute irrelevant
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated(since = "6", forRemoval = true)
	String ARTIFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";

	/**
	 * Whether XML mappings should be processed.
	 *
	 * @apiNote This is a performance optimization appropriate when mapping details
	 * are defined exclusively using annotations.
	 *
	 * @settingDefault {@code true} - XML mappings are processed
	 *
	 * @since 5.4.1
	 */
	String XML_MAPPING_ENABLED = "hibernate.xml_mapping_enabled";

	/**
	 * Specifies the {@link CollectionClassification} to use for a plural attribute
	 * typed as {@link java.util.List} with no explicit list index details
	 * ({@link OrderColumn}, {@link ListIndexBase}, etc.).
	 * <p>
	 * Accepts any of:
	 * <ul>
	 *     <li>an instance of {@code CollectionClassification}
	 *     <li>the (case insensitive) name of a {@code CollectionClassification} (list e.g.)
	 *     <li>a {@link Class} representing either {@link java.util.List} or {@link java.util.Collection}
	 * </ul>
	 *
	 * @settingDefault {@link CollectionClassification#BAG}
	 *
	 * @since 6.0
	 *
	 * @see org.hibernate.annotations.Bag
	 */
	String DEFAULT_LIST_SEMANTICS = "hibernate.mapping.default_list_semantics";

	/**
	 * Enable instantiation of composite/embedded objects when all attribute values
	 * are {@code null}. The default (and historical) behavior is that a {@code null}
	 * reference will be used to represent the composite value when all of its
	 * attributes are {@code null}.
	 *
	 * @apiNote This is an experimental feature that has known issues. It should not
	 *          be used in production until it is stabilized. See Hibernate JIRA issue
	 *          HHH-11936 for details.
	 *
	 * @deprecated It makes no sense at all to enable this at the global level for a
	 *             persistence unit. If anything, it could be a setting specific to
	 *             a given embeddable class. But, four years after the introduction of
	 *             this feature, it's still marked experimental and has multiple known
	 *             unresolved bugs. It's therefore time for those who advocated for
	 *             this feature to accept defeat.
	 *
	 * @since 5.1
	 */
	@Incubating
	@Deprecated(since = "6")
	String CREATE_EMPTY_COMPOSITES_ENABLED = "hibernate.create_empty_composites.enabled";

	/**
	 * The {@link org.hibernate.annotations.Where @Where} annotation specifies a
	 * restriction on the table rows which are visible as entity class instances or
	 * collection elements.
	 * <p>
	 * This setting controls whether the restriction applied to an entity should be
	 * applied to association fetches (for one-to-one, many-to-one, one-to-many, and
	 * many-to-many associations) which target the entity.
	 *
	 * @apiNote The setting is very misnamed - it applies across all entity associations,
	 *          not only to collections.
	 *
	 * @implSpec Enabled ({@code true}) by default, meaning the restriction is applied.
	 *           When this setting is explicitly disabled ({@code false}), the restriction
	 *           is not applied.
	 *
	 * @deprecated Originally added as a backwards compatibility flag
	 */
	@Remove
	@Deprecated( forRemoval = true, since = "6.2" )
	String USE_ENTITY_WHERE_CLAUSE_FOR_COLLECTIONS = "hibernate.use_entity_where_clause_for_collections";

	/**
	 * Whether XML should be validated against their schema as Hibernate reads them.
	 *
	 * @settingDefault {@code true}
	 *
	 * @since 6.1
	 */
	String VALIDATE_XML = "hibernate.validate_xml";

	/**
	 * Enables processing {@code hbm.xml} mappings by transforming them to {@code mapping.xml} and using
	 * that processor.
	 *
	 * @settingDefault {@code false} (opt-in).
	 *
	 * @since 6.1
	 */
	String TRANSFORM_HBM_XML = "hibernate.transform_hbm_xml.enabled";

	/**
	 * How features in a {@code hbm.xml} file which are not supported for transformation
	 * should be handled.  Valid values are defined by {@link UnsupportedFeatureHandling}
	 *
	 * @settingDefault {@link UnsupportedFeatureHandling#ERROR}
	 *
	 * @since 6.1
	 */
	String TRANSFORM_HBM_XML_FEATURE_HANDLING = "hibernate.transform_hbm_xml.unsupported_feature_handling";

	/**
	 * @see org.hibernate.boot.MetadataBuilder#enableImplicitForcingOfDiscriminatorsInSelect
	 *
	 * @settingDefault {@code false}
	 */
	String FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT = "hibernate.discriminator.force_in_select";

	/**
	 * Controls whether Hibernate should infer a discriminator for entity hierarchies
	 * defined with joined inheritance.
	 * <p>
	 * Hibernate does not need a discriminator with joined inheritance.  Therefore, its legacy
	 * behavior is to not infer a discriminator.  However, some JPA providers do require
	 * discriminators with joined inheritance, so in the interest of portability this option
	 * has been added to Hibernate.  When enabled ({@code true}), Hibernate will treat the absence
	 * of discriminator metadata as an indication to use the JPA defined defaults for discriminators.
	 *
	 * @implNote See Hibernate Jira issue HHH-6911 for additional background info.
	 *
	 * @settingDefault {@code false}
	 *
	 * @see org.hibernate.boot.MetadataBuilder#enableImplicitDiscriminatorsForJoinedSubclassSupport
	 * @see #IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
	 */
	String IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS = "hibernate.discriminator.implicit_for_joined";

	/**
	 * Controls whether Hibernate should ignore explicit discriminator metadata with
	 * joined inheritance.
	 * <p>
	 * Hibernate does not need a discriminator with joined inheritance.  Historically
	 * it simply ignored discriminator metadata.  When enabled ({@code true}), any
	 * discriminator metadata ({@link DiscriminatorColumn}, e.g.) is ignored allowing
	 * for backwards compatibility.
	 *
	 * @implNote See Hibernate Jira issue HHH-6911 for additional background info.
	 *
	 * @settingDefault {@code false}
	 *
	 * @see org.hibernate.boot.MetadataBuilder#enableExplicitDiscriminatorsForJoinedSubclassSupport
	 * @see #IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
	 */
	String IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS = "hibernate.discriminator.ignore_explicit_for_joined";

	/**
	 * By default, Hibernate maps character data represented by {@link String}s and
	 * {@link java.sql.Clob}s to the JDBC types {@link java.sql.Types#VARCHAR} and
	 * {@link java.sql.Types#CLOB}. This setting, when enabled, turns on the use of
	 * explicit nationalized character support for mappings involving character
	 * data, specifying that the JDBC types {@link java.sql.Types#NVARCHAR} and
	 * {@link java.sql.Types#NCLOB} should be used instead.
	 * <p>
	 * This setting is relevant for use with databases with
	 * {@linkplain org.hibernate.dialect.NationalizationSupport#EXPLICIT explicit
	 * nationalization support}, and it is not needed for databases whose native
	 * {@code varchar} and {@code clob} types support Unicode data. (If you're not
	 * sure how your database handles Unicode, check out the implementation of
	 * {@link Dialect#getNationalizationSupport()} for its
	 * SQL dialect.)
	 * <p>
	 * Enabling this setting has two effects:
	 * <ol>
	 *     <li>when interacting with JDBC, Hibernate uses operations like
	 *         {@link java.sql.PreparedStatement#setNString(int, String)}
	 *         {@link java.sql.PreparedStatement#setNClob(int, java.sql.NClob)}
	 *         to pass character data, and
	 *     <li>when generating DDL, the schema export tool uses {@code nchar},
	 *         {@code nvarchar}, or {@code nclob} as the generated column
	 *         type when no column type is explicitly specified using
	 *         {@link jakarta.persistence.Column#columnDefinition()}.
	 * </ol>
	 *
	 * @apiNote This is a global setting applying to all mappings associated with a given
	 * {@link SessionFactory}. The {@link Nationalized} annotation may be used to
	 * selectively enable nationalized character support for specific columns.
	 *
	 * @settingDefault {@code false} (disabled)
	 *
	 * @see org.hibernate.boot.MetadataBuilder#enableGlobalNationalizedCharacterDataSupport(boolean)
	 * @see Dialect#getNationalizationSupport
	 * @see Nationalized
	 */
	String USE_NATIONALIZED_CHARACTER_DATA = "hibernate.use_nationalized_character_data";

}
