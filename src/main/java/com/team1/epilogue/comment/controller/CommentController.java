package com.team1.epilogue.comment.controller;

import com.team1.epilogue.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

}
