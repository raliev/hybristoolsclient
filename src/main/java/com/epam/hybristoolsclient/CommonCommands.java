package com.epam.hybristoolsclient;

import com.beust.jcommander.Parameter;

/**
 * Created by Rauf_Aliev on 8/17/2016.
 */
public class CommonCommands {
    @Parameter(names = {"-?", "--help", "-help"}, description = "Help")
    public boolean help = false;
}
