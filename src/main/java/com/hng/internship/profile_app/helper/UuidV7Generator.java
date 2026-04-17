package com.hng.internship.profile_app.helper;

import com.fasterxml.uuid.Generators;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.util.UUID;

public class UuidV7Generator extends SequenceStyleGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sesssion, Object object){
        UUID uuuid = Generators.timeBasedEpochGenerator().generate();
        return uuuid;
    }
}
