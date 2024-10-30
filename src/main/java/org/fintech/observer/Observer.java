package org.fintech.observer;


import org.fintech.dto.AbstractDto;

import java.time.LocalDateTime;

public interface Observer<T extends AbstractDto> {

   void handleEvent(LocalDateTime time, T dto);
}
