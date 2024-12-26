package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.repository.UserRepository;

public interface SpentTimeApi {

    UserCache getUserCache();

    UserService getUserService();

    UserRepository getUserRepository();

}