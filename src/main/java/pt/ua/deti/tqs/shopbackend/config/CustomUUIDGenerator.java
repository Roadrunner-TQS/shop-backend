package pt.ua.deti.tqs.shopbackend.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;
import java.util.UUID;

public class CustomUUIDGenerator extends UUIDGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        Object id = session.getEntityPersister(null, object).getIdentifier(object, session);
        if (id == null) {
            return UUID.randomUUID();
        } else {
            return (Serializable) id;
        }
    }
}

