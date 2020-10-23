package com.diabolickal.pickpocketinfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResetType
{
    LOGOUT("Logout"),
    EXIT("Exit");

    private String name;
}
