package com.example.ex1.dto;

import java.util.List;

public record PollDto(int id, String question, List<OptionDto> options) {}

