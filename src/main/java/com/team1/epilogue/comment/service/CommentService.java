package com.team1.epilogue.comment.service;

import com.team1.epilogue.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;

}
