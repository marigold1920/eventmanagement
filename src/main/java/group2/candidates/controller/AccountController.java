package group2.candidates.controller;

import group2.candidates.model.data.Account;
import group2.candidates.model.data.Authority;
import group2.candidates.service.AccountService;
import group2.candidates.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("account")
public class AccountController {
    private AccountService accountService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AuthorityService authorityService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    @Autowired
    public void setbCrypt(BCryptPasswordEncoder passwordEncoder) {
        this.bCryptPasswordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @GetMapping("/{username}")
    public Account getMyAccount(@PathVariable("username") String username) {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        //String username = (String) token.getPrincipal();
        Account account = null;
        try {
            if (username != null)
                account = accountService.findByUsername(username);
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
        return account;
    }
    /**
     * get all accounts
     *
     * @return Set all accounts
     */
    @GetMapping(produces = {"application/json;charset=UTF-8"})
    public List<Account> getAllAccount() {
        return accountService.loadAllAccounts();
    }

    /**
     * Add a new account to system.
     *
     * @param account the new account
     * @return Added account if add success, otherwise return null.
     */
    @PostMapping(produces = {"application/json;**charset=UTF-8**"})
    public Account saveAccount(@RequestBody Account account) {
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        account.setEnabled(true);
        return accountService.addAccount(account);
    }

    /**
     * Update the new account.
     *
     * @param account the updated account.
     * @return Updated account if add success, otherwise return null.
     */
    @PatchMapping(produces = {"application/json;**charset=UTF-8**"})
    public Account updateAccount(@RequestBody Account account) {
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        return accountService.updateAccount(account);
    }

    @DeleteMapping(value = "{username}",produces = {"application/json;**charset=UTF-8**"})
    public boolean deleteAccount(@PathVariable("username") String username){
        return accountService.deleteAccountByUsername(username);
    }

    /**
     * Disable an account.
     *
     * @param username the account's username.
     * @return Disabled account if add success, otherwise return null.
     */
    @PatchMapping(value = "/disable/{username}")
    public Account disableAccount(@PathVariable("username") String username) {
        return accountService.disableAccount(username);
    }

    /**
     * Enable an account
     *
     * @param username the account's username
     * @return Enabled account if add success, otherwise return null.
     */
    @PatchMapping(value = "/enable/{username}")
    public Account enableAccount(@PathVariable("username") String username) {
        return accountService.enableAccount(username);
    }

    /**
     * @param authorities the array account's authority ids.
     * @param username      the username.
     * @return Updated account if add success, otherwise return null.
     */
    @PatchMapping(value = "update-roles", produces = {"application/json;**charset=UTF-8**"})
    public Account updateRoles(@RequestBody Integer[] authorities, @Param("username") String username) {
        List<Authority> authorityList = new ArrayList<>();
        for (Integer id: authorities) {
            authorityList.add(authorityService.getAuthorityById(id));
        }
        return accountService.updateRole(username, authorityList);
    }
}
