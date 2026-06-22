package strayfurther.book.manager.config;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Configuration
public class GraphqlScalarConfig {

    @Bean
    RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType dateTimeScalar = GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("ISO-8601 timestamp mapped to java.time.Instant")
                .coercing(new Coercing<Instant, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof Instant instant) {
                            return instant.toString();
                        }
                        throw new CoercingSerializeException("Expected an Instant for DateTime serialization");
                    }

                    @Override
                    public Instant parseValue(Object input) {
                        if (input instanceof String value) {
                            try {
                                return Instant.parse(value);
                            } catch (DateTimeParseException ex) {
                                throw new CoercingParseValueException("Invalid ISO-8601 DateTime value", ex);
                            }
                        }
                        throw new CoercingParseValueException("Expected a String for DateTime input");
                    }

                    @Override
                    public Instant parseLiteral(Object input) {
                        if (input instanceof StringValue value) {
                            try {
                                return Instant.parse(value.getValue());
                            } catch (DateTimeParseException ex) {
                                throw new CoercingParseLiteralException("Invalid ISO-8601 DateTime literal", ex);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected a StringValue for DateTime literal");
                    }
                })
                .build();

        return wiringBuilder -> wiringBuilder.scalar(dateTimeScalar);
    }
}

