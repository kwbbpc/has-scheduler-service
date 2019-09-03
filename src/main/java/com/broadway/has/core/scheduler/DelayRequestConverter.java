package com.broadway.has.core.scheduler;

import com.broadway.has.core.repositories.DelayDao;
import com.broadway.has.core.requests.DelayRequest;
import org.joda.time.DateTime;

public class DelayRequestConverter {

    public static final String USER_REQUEST = "User requested delay";

    public static DelayDao convert(DelayRequest request, String reason){

        DelayDao dao = new DelayDao();
        dao.setDelayStartTimestamp(DateTime.now().toDate());
        dao.setDelayEndTimestamp(request.getDelayUntilTimestamp());
        dao.setValveNumber(request.getValveNumber());
        dao.setModifiedTimestamp(DateTime.now().toDate());
        dao.setReason(reason);

        return dao;

    }
}
