package com.ironhack.BankSystem.controller.interfaces;

import com.ironhack.BankSystem.controller.impl.ThirdPartyControllerImpl;
import com.ironhack.BankSystem.model.dto.ThirdPartyDto;
import com.ironhack.BankSystem.model.dto.ThirdPartyTransactionDto;
import com.ironhack.BankSystem.model.modelView.UserMV;
import com.ironhack.BankSystem.model.user.User;
import com.ironhack.BankSystem.service.ThirdPartyService;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

public interface ThirdPartyController {

    public UserMV createThirdParty(ThirdPartyDto thirdPartyDto);
    public void debitThirdParty(String hashKey, ThirdPartyTransactionDto thirdPartyTransactionDto, User user);
    public void creditThirdParty(String hashKey, ThirdPartyTransactionDto thirdPartyTransactionDto, User user);
}
