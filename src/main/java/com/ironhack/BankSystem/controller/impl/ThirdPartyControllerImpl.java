package com.ironhack.BankSystem.controller.impl;

import com.ironhack.BankSystem.controller.interfaces.ThirdPartyController;
import com.ironhack.BankSystem.model.dto.ThirdPartyDto;
import com.ironhack.BankSystem.model.dto.ThirdPartyTransactionDto;
import com.ironhack.BankSystem.model.modelView.UserMV;
import com.ironhack.BankSystem.model.user.User;
import com.ironhack.BankSystem.service.ThirdPartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Third Party Controller")
@RestController
@RequestMapping("/")
public class ThirdPartyControllerImpl implements ThirdPartyController {
    @Autowired
    ThirdPartyService thirdPartyService;

    private static final Logger LOGGER = LogManager.getLogger(ThirdPartyControllerImpl.class);

    @PostMapping("/third-party")
    @ApiOperation(value = "Create a Third Party",
            notes = "Create a Third Party",
            response = User.class)
    @ResponseStatus(HttpStatus.CREATED)
    public UserMV createThirdParty(@RequestBody @Valid ThirdPartyDto thirdPartyDto){
        LOGGER.info("Attempting to create a new Third Party");
        return thirdPartyService.createThirdParty(thirdPartyDto);
    }

    @PostMapping("/third-party/debit/{hashkey}")
    @ApiOperation(value = "Debit Account from Third Party",
            notes = "Debit Account from Third Party")
    @ResponseStatus(HttpStatus.OK)
    public void debitThirdParty(@PathVariable(name = "hashkey" ) String hashKey,
                                                 @RequestBody @Valid ThirdPartyTransactionDto thirdPartyTransactionDto,
                                                 @AuthenticationPrincipal User user){
        LOGGER.info("Third Party " + user.getUsername() + " attempting to debit Account " + thirdPartyTransactionDto.getAccountId() + " with " + thirdPartyTransactionDto.getAmount() + "EUR");
        thirdPartyService.debitThirdParty(hashKey, thirdPartyTransactionDto, user);
    }

    @PostMapping("/third-party/credit/{hashkey}")
    @ApiOperation(value = "Credit Account from Third Party",
            notes = "Credit Account from Third Party")
    @ResponseStatus(HttpStatus.OK)
    public void creditThirdParty(@PathVariable(name = "hashkey" ) String hashKey,
                                                  @RequestBody @Valid ThirdPartyTransactionDto thirdPartyTransactionDto,
                                                  @AuthenticationPrincipal User user){
        LOGGER.info("Third Party " + user.getUsername() + " attempting to credit Account " + thirdPartyTransactionDto.getAccountId() + " with " + thirdPartyTransactionDto.getAmount() + "EUR");
        thirdPartyService.creditThirdParty(hashKey, thirdPartyTransactionDto, user);
    }
}
