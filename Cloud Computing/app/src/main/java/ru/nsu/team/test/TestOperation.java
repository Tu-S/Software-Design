package ru.nsu.team.test;

import ru.nsu.team.client.CloudOperation;

public class TestOperation extends CloudOperation<Integer[], String[]> {
    @Override
    public String[] execute(Integer[] input) {
        var ans = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            ans[i] = String.format("%s%s", input[i].toString(), input[i].toString());
        }
        return ans;
    }
}
